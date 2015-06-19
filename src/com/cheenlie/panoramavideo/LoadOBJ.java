package com.cheenlie.panoramavideo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.content.res.Resources;

public class LoadOBJ {
	public static float[] vertices;
	public static float[] vNormal;
	public static float[] vTexCoord;
	public LoadOBJ(Resources r, String filename, float scale){
		loadObj(r, filename, scale);
	}
	public void loadObj(Resources r, String filename, float scale)
	{
		ArrayList<Float> vArray = new ArrayList<Float>();
		ArrayList<Float> vnArray = new ArrayList<Float>();
		ArrayList<Float> vtArray = new ArrayList<Float>();
		ArrayList<Float> fvArray = new ArrayList<Float>();
		ArrayList<Float> fvnArray = new ArrayList<Float>();
		ArrayList<Float> fvtArray = new ArrayList<Float>();
		
		try {
			InputStream in = r.getAssets().open(filename);   //字节流
			//将字节流转换成字符流
			InputStreamReader isr = new InputStreamReader(in);
			//讲字符流进一步封装
			BufferedReader br = new BufferedReader(isr);
			String temps = null;
			while((temps = br.readLine()) != null){
				String[] tems = temps.split("[ ]+");
				if(tems[0].trim().equals("v")){
					vArray.add(Float.parseFloat(tems[1]));
					vArray.add(Float.parseFloat(tems[2]));
					vArray.add(Float.parseFloat(tems[3]));
				}
				else if(tems[0].trim().equals("vn")){
					vnArray.add(Float.parseFloat(tems[1]));
					vnArray.add(Float.parseFloat(tems[2]));
					vnArray.add(Float.parseFloat(tems[3]));
				}
				else if(tems[0].trim().equals("vt")){
					vtArray.add(Float.parseFloat(tems[1]));
					vtArray.add(Float.parseFloat(tems[2]));
				}
				else if(tems[0].trim().equals("f")){
					//存入三角形数据
					for(int i = 1; i <= 3; i ++){
						String[] st = tems[i].split("/");
						int vIndex = Integer.parseInt(st[0]) - 1;
						int vtIndex = Integer.parseInt(st[1]) - 1;
						int vnIndex = Integer.parseInt(st[2]) - 1;
						//读取顶点
						fvArray.add(vArray.get(vIndex * 3));
						fvArray.add(vArray.get(vIndex * 3 + 1));
						fvArray.add(vArray.get(vIndex * 3 + 2));
						//读取法线向量
						fvnArray.add(vnArray.get(vnIndex * 3));
						fvnArray.add(vnArray.get(vnIndex * 3 + 1));
						fvnArray.add(vnArray.get(vnIndex * 3 + 2));
						//读取纹理坐标
						fvtArray.add(vtArray.get(vtIndex * 2));
						fvtArray.add(vtArray.get(vtIndex * 2 + 1));					
					}//for
				}	
				else;//else 
			}//while
			//将ArrayList 转换成 float[]
			int mVSize = fvArray.size();
			int mTSize = fvtArray.size();
			vertices = new float[mVSize];
			vNormal = new float[mVSize];
			vTexCoord = new float[mTSize];
			for(int j = 0; j < mVSize; j ++){
				vertices[j] = fvArray.get(j) * scale;
				vNormal[j] = fvnArray.get(j);
			}
			for(int j = 0; j < mTSize; j ++){
				vTexCoord[j] = fvtArray.get(j);
			}			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
