package star.smscore.codec.cmpp.packet;

/**
 *
 * @author huzorro(huzorro@gmail.com)
 */
public interface Head {
    public DataType getDataType();
    public int getLength();
    public int getHeadLength();
}
