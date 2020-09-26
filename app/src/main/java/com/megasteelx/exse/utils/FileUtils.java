package com.megasteelx.exse.utils;
import java.io.*;
import java.util.*;
import java.util.zip.*;
import java.security.*;
import java.nio.channels.*;
import android.graphics.*;

public class FileUtils
{

	public static boolean saveFile(Bitmap bm,String path){
        File dirFile = new File(path).getParentFile();
        if(!dirFile.exists()){
            dirFile.mkdir();
        }
		boolean success=false;
        File imgFile = new File(path);
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(imgFile));
        //TODO png format
			bm.compress(Bitmap.CompressFormat.JPEG,80,bos);
            bos.flush();
            bos.close();
			success=true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
			LogUtils.e(e.toString());
        } catch (IOException e) {
            e.printStackTrace();
			LogUtils.e(e.toString());
        }
        return success;
    }
	
	public static String FileToHush(File source){
		String data = FileToString(source.getPath());

		try
		{
			MessageDigest md = MessageDigest.getInstance("md5");
			byte[] md5 = md.digest(data.getBytes());
			return Base64.encode(md5).toString();
		}
		catch (NoSuchAlgorithmException e)
		{
			LogUtils.e("cannot decode MD5");
			return "error";
		}
	}
	public static void ClearDir(String dir){
		File[] fb=new File(dir).listFiles();
		if(fb.length>0)for(int i=0;i<fb.length;i++)fb[i].delete();

	}
	public static boolean ForcedCopyFile(String from,String aim,boolean isDeleteSource){
		try{
			File f=new File(from),a=new File(aim);
			if(!(f.exists()&&f.isFile()))return false;
			a.mkdirs();a.delete();
			FileInputStream fis=new FileInputStream(f);
			FileOutputStream fos=new FileOutputStream(a);
			FileChannel in = fis.getChannel();//得到对应的文件通道
            FileChannel out = fos.getChannel();//得到对应的文件通道
            in.transferTo(0, in.size(), out);//连接两个通道，并且从in通道读取，然后写入out通道
			fis.close();fos.close();in.close();out.close();
			if(isDeleteSource)f.delete();
			return true;
		}catch(Exception e){
			return false;
		}
	}
	public static String FileToString(String filePath)
	{
		if(! new File(filePath).exists()){
			LogUtils.e(filePath+"_not a file");
			return "";
		}
// 读取txt内容为字符串
		StringBuffer txtContent = new StringBuffer();
// 每次读取的byte数
		byte[] b = new byte[8 * 1024];
		InputStream in = null;
		try
		{
// 文件输入流
			in = new FileInputStream(filePath);
			while (in.read(b) != -1)
			{
// 字符串拼接
				txtContent.append(new String(b));
			}
// 关闭流
			in.close();
		}
		catch (FileNotFoundException e)
		{
// TO-DO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
// TO-DO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			if (in != null)
			{
				try
				{
					in.close();
				}
				catch (IOException e)
				{
// TO-DO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return txtContent.toString().trim();
	}
	
	public static void saveStringToFile(String path,String obj,boolean isCoverFile){
		try{
			File f=new File(path);
			if(f.exists()){
			  if(isCoverFile){
				f.delete();
				LogUtils.w(path+"_aim filepath exists.covered.");
			  }else{
				String[] tempPath=path.split("/");
				String[] tempName=tempPath[tempPath.length-1].split(".");
				String newPath="";
				for(int i=0;i<tempPath.length-1;i++){
					newPath+=tempPath[i];
				}newPath+=tempName[0]+"_";
				for(int k=1;k<tempName.length;k++){
					newPath+=tempName[k];
				}
				ForcedCopyFile(path,newPath,true);
				LogUtils.w(path+"_aim filename exists.renamed old file.");
			  }
			}
			FileWriter writer=new FileWriter(path);
			writer.write(obj);
			writer.close();
		}catch(Exception e){}
	}
	public static void zip(String src, String dest) throws IOException {
		//提供了一个数据项压缩成一个ZIP归档输出流
		ZipOutputStream out = null;
		try {File outFile = new File(dest);
			//源文件或者目录
			File fileOrDirectory = new File(src);
			//压缩文件路径
			out = new ZipOutputStream(new FileOutputStream(outFile));
			//如果此文件是一个文件，否则为false。
			if (fileOrDirectory.isFile()) {
				zipFileOrDirectory(out, fileOrDirectory,"");} 
			else {
				//返回一个文件或空阵列。
				File[] entries = fileOrDirectory.listFiles();
				for (int i = 0; i < entries.length; i++) {
					// 递归压缩，更新cur Paths
					zipFileOrDirectory(out, entries[i],"");}}} 
		catch (IOException ex) {
			ex.printStackTrace();} 
		finally {
			//关闭输出流
			if (out != null) {
				try {out.close();
				} catch (IOException ex) {
					ex.printStackTrace();}}}
	}private static void zipFileOrDirectory(ZipOutputStream out,File fileOrDirectory, String curPath) throws IOException {
		//从文件中读取字节的输入流
		FileInputStream in = null;try {
			//如果此文件是一个目录，否则返回false。
			if (!fileOrDirectory.isDirectory()) {
				// 压缩文件
				byte[] buffer = new byte[4096];
				int bytes_read;in = new FileInputStream(fileOrDirectory);
				//实例代表一个条目内的ZIP归档
				ZipEntry entry = new ZipEntry(curPath+ fileOrDirectory.getName());
				//条目的信息写入底层流
				out.putNextEntry(entry);
				while ((bytes_read= in.read(buffer)) != -1) {
					out.write(buffer, 0, bytes_read);
				}out.closeEntry();
			} else {
				// 压缩目录
				File[] entries = fileOrDirectory.listFiles();
				for (int i = 0; i < entries.length; i++) {
					// 递归压缩，更新curPaths
					zipFileOrDirectory(out, entries[i], curPath+ fileOrDirectory.getName() + '/');}}
		} catch (IOException ex) {
			ex.printStackTrace();
			// throw ex;
		} finally {
			if (in != null) {
				try {in.close();
				} catch (IOException ex) {
					ex.printStackTrace();}}}}

	public static LinkedList<File> listLinkedFiles(String strPath) {
        LinkedList<File> list = new LinkedList<File>();
        File dir = new File(strPath);
        File file[] = dir.listFiles();
        for (int i = 0; i < file.length; i++) {
            if (file[i].isDirectory())
                list.add(file[i]);
            else
                System.out.println(file[i].getAbsolutePath());
        }
        File tmp;
        while (!list.isEmpty()) {
            tmp = list.removeFirst();
            if (tmp.isDirectory()) {
                file = tmp.listFiles();
                if (file == null)
                    continue;
                for (int i = 0; i < file.length; i++) {
                    if (file[i].isDirectory())
                        list.add(file[i]);
                    else
                        System.out.println(file[i].getAbsolutePath());
                }
            } else {
                System.out.println(tmp.getAbsolutePath());
            }
        }
        return list;
    }

    //recursion
    public static ArrayList<File> listFiles(String strPath) {
        return refreshFileList(strPath);
    }

    public static ArrayList<File> refreshFileList(String strPath) {
        ArrayList<File> filelist = new ArrayList<File>();
        File dir = new File(strPath);
        File[] files = dir.listFiles();
        if (files == null)
            return null;
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                refreshFileList(files[i].getAbsolutePath());
            } else {
                if(files[i].getName().toLowerCase().endsWith("zip"))
                    filelist.add(files[i]);
            }
        }
        return filelist;
    }
	//TODO 把压缩/解压换成异步方法
	private static final int BUFF_SIZE = 1024 * 1024; // 1M Byte
	/**
     * 解压缩一个文件
     *
     * @param zipFile 压缩文件
     * @param folderPath 解压缩的目标目录
     * @throws IOException 当解压缩过程出错时抛出
     */
    public static void upZipFile(File zipFile, String folderPath) throws ZipException, IOException {
        File desDir = new File(folderPath);
        if (!desDir.exists()) {
            desDir.mkdirs();
        }
		File srcf=zipFile;
		boolean hasTempFile=false;
		File trans=new File(folderPath+"upziptempfile");
		FileInputStream srcin=new FileInputStream(srcf);
		if(srcin.read()!=0x50){
			hasTempFile=true;
			FileOutputStream transout=new FileOutputStream(trans);
			boolean flag=false;
			byte[]aimchar=new byte[]{0x50,0x4b,0x03,0x04};
			transout.write(aimchar);
			byte[]tempchar=new byte[]{0,0,0,0};
			for(int index=0;index<srcf.length();index++){
				if(!flag){
					tempchar[0]=tempchar[1];
					tempchar[1]=tempchar[2];
					tempchar[2]=tempchar[3];
					tempchar[3]=(byte) srcin.read();
					if(Arrays.equals(tempchar,aimchar)){flag=true;}
				}else{
					transout.write(srcin.read());
				}
			}
			srcin.close();transout.close();
			zipFile=trans;
		}
        ZipFile zf = new ZipFile(zipFile);
        for (Enumeration<?> entries = zf.entries(); entries.hasMoreElements();) {
            ZipEntry entry = ((ZipEntry)entries.nextElement());
            InputStream in = zf.getInputStream(entry);
            String str = folderPath + File.separator + entry.getName();
            str = new String(str.getBytes("8859_1"), "GB2312");
            File desFile = new File(str);
            if (!desFile.exists()) {
                File fileParentDir = desFile.getParentFile();
                if (!fileParentDir.exists()) {
                    fileParentDir.mkdirs();
                }
                desFile.createNewFile();
            }
            OutputStream out = new FileOutputStream(desFile);
            byte buffer[] = new byte[BUFF_SIZE];
            int realLength;
            while ((realLength = in.read(buffer)) > 0) {
                out.write(buffer, 0, realLength);
            }
            in.close();
            out.close();
        }
		if(hasTempFile)trans.delete();
    }

    /**
     * 解压文件名包含传入文字的文件
     *
     * @param zipFile 压缩文件
     * @param folderPath 目标文件夹
     * @param nameContains 传入的文件匹配名
     * @throws ZipException 压缩格式有误时抛出
     * @throws IOException IO错误时抛出
     */
    public static ArrayList<File> upZipSelectedFile(File zipFile, String folderPath,
													String nameContains) throws ZipException, IOException {
        ArrayList<File> fileList = new ArrayList<File>();

        File desDir = new File(folderPath);
        if (!desDir.exists()) {
            desDir.mkdir();
        }

        ZipFile zf = new ZipFile(zipFile);
        for (Enumeration<?> entries = zf.entries(); entries.hasMoreElements();) {
            ZipEntry entry = ((ZipEntry)entries.nextElement());
            if (entry.getName().contains(nameContains)) {
                InputStream in = zf.getInputStream(entry);
                String str = folderPath + File.separator + entry.getName();
                str = new String(str.getBytes("8859_1"), "GB2312");
                // str.getBytes("GB2312"),"8859_1" 输出
                // str.getBytes("8859_1"),"GB2312" 输入
                File desFile = new File(str);
                if (!desFile.exists()) {
                    File fileParentDir = desFile.getParentFile();
                    if (!fileParentDir.exists()) {
                        fileParentDir.mkdirs();
                    }
                    desFile.createNewFile();
                }
                OutputStream out = new FileOutputStream(desFile);
                byte buffer[] = new byte[BUFF_SIZE];
                int realLength;
                while ((realLength = in.read(buffer)) > 0) {
                    out.write(buffer, 0, realLength);
                }
                in.close();
                out.close();
                fileList.add(desFile);
            }
        }
        return fileList;
    }
}
