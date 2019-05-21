package com.androidex.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * 序列化工具类
 * Created by yihaibin on 16/9/18.
 */
public class SeriaUtil {

    /**
     * 克隆对象，克隆对象以及其包含的属性必须是实现了序列化接口
     * @param obj
     * @param <T>
     * @return
     */
    public static <T extends Serializable> T clone(T obj){

        if(obj == null)
            return null;

        ByteArrayOutputStream byteArrayOutPut = null;
        ObjectOutputStream objOutput = null;
        ByteArrayInputStream byteArrayInput = null;
        ObjectInputStream objInput = null;
        try {

            //将对象写到流里
            byteArrayOutPut = new ByteArrayOutputStream();
            objOutput  = new ObjectOutputStream(byteArrayOutPut);
            objOutput.writeObject(obj);
            //从流里读出来
            byteArrayInput = new ByteArrayInputStream(byteArrayOutPut.toByteArray());
            objInput = new ObjectInputStream(byteArrayInput);
            return (T) objInput.readObject();

        } catch (Exception e) {

            if(LogMgr.isDebug())
                e.printStackTrace();
        }finally {

            IOUtil.closeOutStream(byteArrayOutPut);
            IOUtil.closeOutStream(objOutput);
            IOUtil.closeInStream(byteArrayInput);
            IOUtil.closeInStream(objInput);
        }

        return null;
    }
}
