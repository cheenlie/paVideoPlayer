package com.cheenlie.panoramavideo;

import java.io.IOException;
import java.io.InputStream;

import android.media.MediaMetadataRetriever;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.GLES20;
import android.os.Environment;
import android.view.MotionEvent;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

// 213 纹理初始化

class MySurfaceView extends GLSurfaceView {
    
	private final float TOUCH_SCALE_FACTOR = 180.0f/320;//角度缩放比例
	private float mPreviousY;//上次的触控位置Y坐标
    private float mPreviousX;//上次的触控位置X坐标
	
	private SceneRenderer mRenderer;//场景渲染器
    int textureId;      //系统分配的纹理id 
    
    boolean drawWhatFlag=true;	//绘制线填充方式的标志位
    boolean lightFlag=false;		//光照旋转的标志位
    
    private static final float NS2S = 1.0f/1000000000.0f;
    private float timestamp;

    float tem_x = 0.0f;
    float tem_y = 0.0f;
    float tem_z = 0.0f;
    
    //关于Media的变量
    MediaMetadataRetriever retriever;
    int gSumTime = 0;
    int gNowTime = 0;
    int gTextureId = 0;
    int gFrameSecond = 5;
    int gTimeMs = 0;
    int gNum = 0;
    
	public MySurfaceView(Context context) {
        super(context);
        this.setEGLContextClientVersion(2); //设置使用OPENGL ES2.0
        mRenderer = new SceneRenderer();	//创建场景渲染器
        setRenderer(mRenderer);				//设置渲染器		        
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//设置渲染模式为主动渲染   
    }
	
	//触摸事件回调方法
    @Override 
    public boolean onTouchEvent(MotionEvent e) {
        float y = e.getY();
        float x = e.getX();
        switch (e.getAction()) {
        case MotionEvent.ACTION_MOVE:
            float dy = y - mPreviousY;//计算触控笔Y位移
            float dx = x - mPreviousX;//计算触控笔X位移
            mRenderer.triangle.yAngle += dx * TOUCH_SCALE_FACTOR;//设置绕y轴旋转角度
            mRenderer.triangle.xAngle += dy * TOUCH_SCALE_FACTOR;//设置绕z轴旋转角度
            
            //mRenderer.triangle.zAngle += dy * TOUCH_SCALE_FACTOR;//设置绕y轴旋转角度
            //mRenderer.regular20l.zAngle+= dy * TOUCH_SCALE_FACTOR;//设置绕z轴旋转角度
        }
        mPreviousY = y;//记录触控笔位置
        mPreviousX = x;//记录触控笔位置
        return true;
    }
    
	private class SceneRenderer implements GLSurfaceView.Renderer 
    {   
		
		Triangle triangle;
		//Regular20L regular20l;
		
        public void onDrawFrame(GL10 gl) 
        { 
//        	Long lBegin = System.currentTimeMillis();
        	//清除深度缓冲与颜色缓冲
            GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);   
            triangle.zAngle += tem_z;
            triangle.xAngle += tem_y;
            triangle.yAngle -= tem_x;
            
///* Debug            
            //添加纹理
			if (gNowTime < gSumTime) {
				
				GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
				int lFrame = gNowTime * 1000 * 1000	
						+ 1000 * gTimeMs * 1000 / gFrameSecond;
				Bitmap bitmapTmp = retriever.getFrameAtTime(
						lFrame,				
						MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
				
				
				GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, // 纹理类型，在OpenGL
															// ES中必须为GL10.GL_TEXTURE_2D
						0, // 纹理的层次，0表示基本图像层，可以理解为直接贴图
					   	bitmapTmp, // 纹理图像
						0 // 纹理边框尺寸
				);
				Long lEnd = System.currentTimeMillis();
				//gNum ++;
//				System.out.println(lEnd);
//				System.out.println(lEnd-lBegin);
				bitmapTmp.recycle(); // 纹理加载成功后释放图片
				gTimeMs ++;
				if(gTimeMs >= gFrameSecond){
					gTimeMs = 0;
					gNowTime ++;
				}
			}
//*/
            
//          !!!!!!!!!            
//          Bitmap bitmapTmp = ??? 
//			GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, // 纹理类型，在OpenGL
//					// ES中必须为GL10.GL_TEXTURE_2D
//					0, // 纹理的层次，0表示基本图像层，可以理解为直接贴图
//					bitmapTmp, // 纹理图像
//					0 // 纹理边框尺寸
//			);
            
            //保护现场
            MatrixState.pushMatrix();
            
            //MatrixState.translate(0, 0, -10);
            
            if(drawWhatFlag)
            {
            	
//              MatrixState.rotate(triangle.xAngle, 1, 0, 0);
//           	MatrixState.rotate(triangle.yAngle, 0, 1, 0);
//           	MatrixState.rotate(triangle.zAngle, 0, 0, 1);
            	triangle.drawSelf(textureId);
            	
            }
            else
            {
            	//regular20l.drawSelf();
            }
            
            MatrixState.popMatrix();
//            Long lEnd = System.currentTimeMillis();
//            System.out.println(lEnd-lBegin);
        }   

        public void onSurfaceChanged(GL10 gl, int width, int height) {
            //设置视窗大小及位置 
        	GLES20.glViewport(0, 0, width, height); 
        	//计算GLSurfaceView的宽高比
            float ratio= (float) width / height;
            //调用此方法计算产生透视投影矩阵
            MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 1.0f, 4.0f);
            //调用此方法产生摄像机9参数位置矩阵
            MatrixState.setCamera(0,0,0.1f,0f,0f,0f,0f,1.0f,0.0f); 
            
	        //初始化光源
	        MatrixState.setLightLocation(0.1f , 0.1f , 0.1f);
	                      
//	        //启动一个线程定时修改灯光的位置
//	        new Thread()
//	        {
//				public void run()
//				{
//					float redAngle = 0;
//					while(lightFlag)
//					{	
//						//根据角度计算灯光的位置
//						redAngle=(redAngle+5)%360;
//						float rx=(float) (15*Math.sin(Math.toRadians(redAngle)));
//						float rz=(float) (15*Math.cos(Math.toRadians(redAngle)));
//						MatrixState.setLightLocation(rx, 0, rz);
//						
//						try {
//								Thread.sleep(40);
//							} catch (InterruptedException e) {				  			
//								e.printStackTrace();
//							}
//					}
//				}
//	        }.start();
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            //设置屏幕背景色RGBA
            GLES20.glClearColor(0.5f,0.5f,0.5f, 1.0f);  
            //启用深度测试
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
    		//设置为打开背面剪裁
            GLES20.glEnable(GLES20.GL_CULL_FACE);
            GLES20.glFrontFace(GLES20.GL_CW);
            //初始化变换矩阵
            MatrixState.setInitStack();
            
            // !!!!!!!!!!!!!!!!!!!!!!!!!!!
            //加载纹理 
//            textureId=initTexture(R.drawable.zhao); //加载图像
            initMediaTexture();                    //加载视频
            
            //创建正20面体对象m
            triangle = new Triangle(MySurfaceView.this, 0.010f);
            //创建正20面体骨架对象
            //triangle= new Triangle(MySurfaceView.this,2,1.6f,5);
            
            //将视频转换为图像
            String mediaPath = Environment.getExternalStorageDirectory() + "/video/360videodemo.MP4";
            retriever = new MediaMetadataRetriever();
            retriever.setDataSource(mediaPath);
            String lTime = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            gSumTime = Integer.valueOf(lTime) / 1000;
        }          
    }
	
	public int initTexture(int drawableId)//textureId
	{
		//生成纹理ID
		int[] textures = new int[1];
		GLES20.glGenTextures
		(
				1,          //产生的纹理id的数量
				textures,   //纹理id的数组
				0           //偏移量
		);    
		int textureId=textures[0];    
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_NEAREST);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_CLAMP_TO_EDGE);
        
        //通过输入流加载图片===============begin===================
        InputStream is = this.getResources().openRawResource(drawableId);
        Bitmap bitmapTmp;
        try 
        {
        	bitmapTmp = BitmapFactory.decodeStream(is);
        } 
        finally 
        {
            try 
            {
                is.close();
            } 
            catch(IOException e) 
            {
                e.printStackTrace();
            }
        }
        //通过输入流加载图片===============end=====================  
        
        //实际加载纹理
        GLUtils.texImage2D
        (
        		GLES20.GL_TEXTURE_2D,   //纹理类型，在OpenGL ES中必须为GL10.GL_TEXTURE_2D
        		0, 					  //纹理的层次，0表示基本图像层，可以理解为直接贴图
        		bitmapTmp, 			  //纹理图像
        		0					  //纹理边框尺寸
        );
        bitmapTmp.recycle(); 		  //纹理加载成功后释放图片
        
        return textureId;
	}
	
	//视频纹理
	public void initMediaTexture()//textureId
	{
		//生成纹理ID
		int[] textures = new int[1];
		GLES20.glGenTextures
		(
				1,          //产生的纹理id的数量
				textures,   //纹理id的数组
				0           //偏移量
		);    
		textureId=textures[0];    
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_NEAREST);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_CLAMP_TO_EDGE);        
        //实际加载纹理
//        GLUtils.texImage2D
//        (
//        		GLES20.GL_TEXTURE_2D,   //纹理类型，在OpenGL ES中必须为GL10.GL_TEXTURE_2D
//        		0, 					  //纹理的层次，0表示基本图像层，可以理解为直接贴图
//        		bitmapTmp, 			  //纹理图像
//        		0					  //纹理边框尺寸
//        );
//        bitmapTmp.recycle(); 		  //纹理加载成功后释放图片
   
	}
	
	public void changed(float x, float y, float z)
    {
		tem_x = x;
		tem_y = y;
		tem_z = z;

//		if(mRenderer.triangle.xAngle > 360.0)
//		{
//			mRenderer.triangle.xAngle -= 360.0f;
//		}
//		if(mRenderer.triangle.xAngle < 0.0)
//		{
//			mRenderer.triangle.xAngle += 360.0f;
//		}
//		if(mRenderer.triangle.yAngle > 360.0)
//		{
//			mRenderer.triangle.yAngle -= 360.0f;
//		}
//		if(mRenderer.triangle.yAngle < 0.0)
//		{
//			mRenderer.triangle.yAngle += 360.0f;
//		}
//		if(mRenderer.triangle.zAngle > 360.0)
//		{
//			mRenderer.triangle.zAngle -= 360.0f;
//		}
//		if(mRenderer.triangle.zAngle < 0.0)
//		{
//			mRenderer.triangle.zAngle += 360.0f;
//		}
		
    }
    
}
