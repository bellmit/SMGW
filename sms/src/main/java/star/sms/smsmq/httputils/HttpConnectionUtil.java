package star.sms.smsmq.httputils;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import star.sms._frame.utils.JsonUtil;
import star.sms._frame.utils.UUIDUtils;
/**
 * 请求连接工具类
 * @author star
 *
 */
@Service
public class HttpConnectionUtil {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
    /**
     * OkHttpClient
     */
    private OkHttpClient okHttpClientSync = null;
    
    /**
     * OkHttpClient
     */
    private OkHttpClient okHttpClientAsync = null;
    
	 /**
     * 发生异常重试连接器重试拦截器
     */
    private class RetryIntercepter implements Interceptor {

        public int maxRetry;//最大重试次数
        private int retryNum = 0;//假如设置为3次重试的话，则最大可能请求4次（默认1次+3次重试）

        public RetryIntercepter(int maxRetry) {
            this.maxRetry = maxRetry;
        }

		@Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Response response = null;
            try {
            	response = chain.proceed(request);
            } catch (Exception e) {
            	e.printStackTrace();
            	 while (retryNum < maxRetry) {
                     retryNum++;
                     response = chain.proceed(request);
                 }
            } 
            return response;
        }
    }
    /**
     * 单例模式得到HTTP连接
     * @return
     */
	private OkHttpClient getHttpClientSync() {
		if (okHttpClientSync == null) {
			synchronized (this) {
				if (okHttpClientSync == null) {
					okHttpClientSync = new OkHttpClient()
							.newBuilder()
							.addInterceptor(new RetryIntercepter(1))
							.readTimeout(15000, TimeUnit.MILLISECONDS)
							.build();
				}
			}
		}
		return okHttpClientSync;
	}
	/**
	 * 同步http
	 * 
	 * @param api
	 * @param requestJson
	 * @return
	 */
	public String postSync(String api, Map<String,String> paramsMap) {
		//流水号
    	String serialNumber = UUIDUtils.random();
    	String requestData = JsonUtil.obj2String(paramsMap);
    	logger.info(" ====> serialNumber: "+serialNumber+" , request(POST): "+api+"  "+requestData);
    	//发起请求
        String result=null;
        
		OkHttpClient okHttpClient = this.getHttpClientSync();
		if(okHttpClient!= null) {
	        //请求body
			FormBody.Builder builder = new FormBody.Builder();
			for (String key : paramsMap.keySet()) {
		    	//追加表单信息
		    	builder.add(key, paramsMap.get(key));
		    }
			RequestBody body = builder.build();
	        //请求header的添加
	        Headers headers = new Headers.Builder().build();
	        //请求组合创建
	        Request request = new Request.Builder()
	                .url(api)
	                .post(body)
	                .headers(headers)
	                .build();
	        
			try {
				Call call = okHttpClient.newCall(request);
				if (call != null) {
					Response response = call.execute();
					if (response != null) {
						ResponseBody responseBody = response.body();
						if (responseBody != null) {
							result = responseBody.string();
						}
					}
				}
			} catch (Exception e) {
				logger.error(" ====> serialNumber: "+serialNumber+" , response(POST): "+result+"  "+ e.getMessage());
				e.printStackTrace();
			}
			logger.info(" <==== serialNumber: "+serialNumber+" , response(POST): "+result);
			if(StringUtils.isBlank(result)) {
				result = null;
			}
		}
        return result;
	}
	/**
     * 单例模式得到HTTP连接
     * @return
     */
	private OkHttpClient getHttpClientAsync() {
		if (okHttpClientAsync == null) {
			synchronized (this) {
				if (okHttpClientAsync == null) {
					okHttpClientAsync = new OkHttpClient()
							.newBuilder()
							.readTimeout(15000, TimeUnit.MILLISECONDS)
							.build();
				}
			}
		}
		return okHttpClientAsync;
	}
    /**
     * 异步http连接
     */
	public void postAsync(String api, String requestData,ResponseCallback responseCallback) {
    	//流水号
    	String serialNumber = UUIDUtils.random();
    	logger.info(" ====> serialNumber: "+serialNumber+" , request(POST): "+api+"  "+requestData);
    	
    	OkHttpClient okHttpClient = this.getHttpClientAsync();
    	
    	if(okHttpClient!=null) {
            //请求body
            MediaType mediaType=MediaType.Companion.parse("application/x-www-form-urlencoded");
            RequestBody body=RequestBody.Companion.create(requestData,mediaType);
            //请求header的添加
            Headers headers = new Headers.Builder().build();
            //请求组合创建
            Request request = new Request.Builder()
                    .url(api)
                    .post(body)
                    .headers(headers)
                    .build();
             //发起请求
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                	e.printStackTrace();
                }
                @Override
    			public void onResponse(Call call, Response response) {
    				try {
    					String result = "";
    					if (response != null) {
    						ResponseBody responseBody = response.body();
    						if (responseBody != null) {
    							result = responseBody.string();
    						}
    					}
    					logger.info(" <==== serialNumber: "+serialNumber+" , response(POST): "+result);
    					//返回数据
    					responseCallback.success(result);
    				} catch (Exception e) {
    					logger.error(" ====> serialNumber: "+serialNumber+" , response(POST): "+api+"  "+ e.getMessage());
    					e.printStackTrace();
    				}
    			}
            });
    	}
    }

}
