package server;

/**
 * Created by GJY on 2016/11/5.
 */
public class Header {

    /**Header length in bytes*/
    public static final int HEADER_SIZE = 5;

    private byte[] data;

    public Header(){
        data = new byte[HEADER_SIZE];
    }

    public  Header(byte[] data){
        this.data = data;
    }

    public void setSynFlag(boolean flag){
        if(flag){
            data[4] |= 1 << 7;
        }else{
            data[4] &= ~(1 << 7);
        }
    }

    public void setAckFlag(boolean flag){
        if(flag){
            data[4] |= 1 << 6;
        }else{
            data[4] &= ~(1 << 6);
        }
    }

    public void setReqFlag(boolean flag){
        if(flag){
            data[4] |= 1 << 5;
        }else{
            data[4] &= ~(1 << 5);
        }
    }

    public void setSequenceNum(int seqNum){
        byte[] converted = intoByteArr(4, seqNum);

        for(int i = 0; i < converted.length; i++){
            data[i] = converted[i];
        }
    }

    public boolean getSynFlag(){
        int x = (1 << 7);
        return (data[4] & x) == x;
    }

    public boolean getAckFlag(){
        int x = (1 << 6);
        return (data[4] & x) == x;
    }

    public boolean getReqFlag(){
        int x = (1 << 5);
        return (data[4] & x) == x;
    }

    public int getSequenceNum(){
        return (int)(data[0] << 24 | data[1] << 16 | data[2] << 8 | data[3] & 0xFF);
    }

    /**transform 32bit int to byteArray*/
    private byte[] intoByteArr(int numBytes,int toConvert){
        byte[] data = new byte[numBytes];
        for(int i = numBytes -1,offset = 0;i >= 0;i--,offset += 8 ){
            data[i] = (byte)(toConvert >> offset);
        }

        return data;
    }

    public byte[] getData(){
        return data;
    }

    public static byte[] createStatusPacket(boolean good,int numPackets,int numBytes){
        Header header = new Header();
        header.setAckFlag(true);

        final int dataLen = 9;
        byte[] dataField = new byte[dataLen];

        dataField[0] = (byte)((good? 1:0) << 7);

        if(good){
            byte[] convertedPacket = header.intoByteArr(4, numPackets);
            byte[] convertedByte = header.intoByteArr(4, numBytes);

            System.arraycopy(convertedPacket, 0, dataField, 1, 4);
            System.arraycopy(convertedByte, 0, dataField, 5, 4);
        }

        byte[] toReturn = new byte[HEADER_SIZE + dataLen];

        for(int i = 0; i < HEADER_SIZE; i++){
            toReturn[i] = header.getData()[i];
        }

        for(int i = 0; i < dataLen; i++){
            toReturn[HEADER_SIZE + i] = dataField[i];
        }

        return  toReturn;
    }


}
