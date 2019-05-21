package com.androidex.util;

import android.content.res.AssetFileDescriptor;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import com.androidex.context.ExApplication;

/**
 * assets资源工具类
 * @author yhb
 */
public class AssetsUtil {

	/**
	 * 获取Asset目录下指定文件的AssetFileDescriptor对象
	 * @param filePath
	 * @return
	 */
	public static AssetFileDescriptor getAssetFileDescriptor(String filePath){

		try{
			return ExApplication.getContext().getAssets().openFd(filePath);
		}catch(Exception e){

			if(LogMgr.isDebug())
				e.printStackTrace();
		}

		return null;
	}

	/**
	 * utf-8编码获取文本
	 * @param assetsPath
	 * @return
	 */
	public static String getTextByUtf8(String assetsPath){

		return getText(assetsPath, "utf-8");
	}

	/**
	 * 根据path从assets目录下获取文本
	 * @param assetsPath
	 * @return
	 */
	public static String getText(String assetsPath, String charSetName){

		BufferedReader br = null;
		try{

			br = new BufferedReader(new InputStreamReader(ExApplication.getContext().getAssets().open(assetsPath), charSetName));

			String line = null;
			StringBuilder sb = new StringBuilder();
			while((line = br.readLine()) != null){

				sb.append(line);
			}

			return sb.toString();

		}catch(Exception e){

			if(LogMgr.isDebug())
				e.printStackTrace();

		}finally{

			IOUtil.closeReader(br);
		}

		return TextUtil.TEXT_EMPTY;
	}

	/**
	 * 聪assets目录拷贝文件至指定路径
	 * @param assetsFilePath
	 * @param destPath
	 * @return
	 */
	public static boolean copyFile(String assetsFilePath, String destPath){

		InputStream is = null;
		OutputStream os = null;

		try{

			os = new FileOutputStream(destPath);
			is = ExApplication.getContext().getAssets().open(assetsFilePath);
			byte[] bytes = new byte[1024];
			int len = -1;
			while((len = is.read(bytes)) != -1){

				os.write(bytes, 0, len);
			}
			return true;

		}catch(Exception e){

			e.printStackTrace();
		}finally {

			IOUtil.closeInStream(is);
			IOUtil.closeOutStream(os);
		}

		return false;
	}
}
