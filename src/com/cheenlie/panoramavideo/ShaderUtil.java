package com.cheenlie.panoramavideo;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import android.content.res.Resources;
import android.opengl.GLES20;
import android.util.Log;

public class ShaderUtil {
	public static int loadShader
	(
			int shaderType,
			String source
	)
	{
		int shader = GLES20.glCreateShader(shaderType);
		if(shader != 0)
		{
			GLES20.glShaderSource(shader, source);
			GLES20.glCompileShader(shader);
			int[] compiled = new int[1];
			GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
			//compiled wrong
			if(compiled[0] == 0)
			{
				Log.e("ES20_ERROR", "Could not compile shader " + shaderType + ":");
				Log.e("GL20_ERROR", GLES20.glGetShaderInfoLog(shader));
				GLES20.glDeleteShader(shader);
				shader = 0;
			}
		}//if shader != 0
		return shader;
	}//LoadShader
	
	//创建shader程序的方法
	public static int createProgram(String vertexSource, String fragmentSource)
	{
		//vertex shader
		int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
		if(vertexShader == 0)
		{
			return 0;
		}
		//fragment shader
		int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
		if(fragmentShader == 0)
		{
			return 0;
		}
		// create program
		int program = GLES20.glCreateProgram();
		if(program != 0)
		{
			//attach vertex shader
			GLES20.glAttachShader(program, vertexShader);
			checkGlError("glAttachShader");
			//attach fragment shader
			GLES20.glAttachShader(program, fragmentShader);
			checkGlError("glAttachShader");
			//link
			GLES20.glLinkProgram(program);
			
			int[] linkStatus = new int[1];
			GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
			
			//if link wrong
			if(linkStatus[0] != GLES20.GL_TRUE)
			{
				Log.e("ES20_ERROR", "Could not link program: ");
				Log.e("ES20_ERROR", GLES20.glGetProgramInfoLog(program));
				GLES20.glDeleteProgram(program);
				program = 0;
			}
			
		}// if program != 0
		return program;
	}//create program
	
	public static void checkGlError(String op)
	{
		int error;
		error = GLES20.glGetError();
		while(error != GLES20.GL_NO_ERROR)
		{
			Log.e("ES20_ERROR", op + ":glError " + error);
			throw new RuntimeException(op + ": glError " + error);
		}//while
	}//Check Error
	
	//load shader from sh Assets
	public static String loadFromAssetsFile(String fname, Resources r)
	{
		String result = null;
		try{
			InputStream in = r.getAssets().open(fname);
			int ch = 0;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			while((ch = in.read()) != -1)
			{
				baos.write(ch);
			}
			byte[] buff = baos.toByteArray();
			baos.close();
			in.close();
			result = new String(buff, "UTF-8");
			result = result.replaceAll("\\r\\n", "\n");
		}//try
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return result;
	}//load sh
}
