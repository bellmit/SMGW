package star.smscore.connect.manager;

import io.netty.util.concurrent.Future;

public interface ExitUnlimitCirclePolicy<T> {
	boolean notOver(Future<T> future);
}
