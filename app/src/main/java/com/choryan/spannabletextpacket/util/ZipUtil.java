package com.choryan.spannabletextpacket.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtil {
	
	public static boolean decompress(String zipFilePath, String aimDir, boolean deleteOld){
		try {
			return decompress(new FileInputStream(zipFilePath), aimDir, deleteOld);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean decompress(InputStream inputStream , String aimDir, boolean deleteOld){
		long startTime= System.currentTimeMillis();
		try {
			ZipInputStream Zin=new ZipInputStream(inputStream);//输入源zip路径
			BufferedInputStream Bin=new BufferedInputStream(Zin);
			File Fout=null;
			ZipEntry entry;
			File file = new File(aimDir);
			if(!file.exists()){
				file.mkdirs();
			}else{
				if(deleteOld){
					file.delete();
					file.mkdirs();
				}
			}
				
			while((entry = Zin.getNextEntry())!=null){
				if(entry.isDirectory()) {
					continue;
				}
				Fout=new File(aimDir,entry.getName());
				if(!Fout.exists()){
					(new File(Fout.getParent())).mkdirs();
				}
				FileOutputStream out=new FileOutputStream(Fout);
				BufferedOutputStream Bout=new BufferedOutputStream(out);
				int b;
				while((b=Bin.read())!=-1){
					Bout.write(b);
				}
				Bout.close();
				out.close();
				System.out.println(Fout+"解压成功");
			}
			Bin.close();
			Zin.close();
			long endTime= System.currentTimeMillis();
			System.out.println("耗费时间： "+(endTime-startTime)+" ms");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}
