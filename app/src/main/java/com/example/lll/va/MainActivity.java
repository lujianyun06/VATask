package com.example.lll.va;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.lll.va.task6.Task6;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        Task3.main(this);
//        Task4.main(this);
//        Task7.main(this);
//        Task5min.main(this);
//        Task5.main(this);
        Task6.main(this);
    }

//
//    private GLSurfaceView mGLSurfaceView;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//
////                if (!Utils.supportGlEs20(this)) {
////                    Toast.makeText(this, "GLES 2.0 not supported!", Toast.LENGTH_LONG).show();
////                    finish();
////                    return;
////                }
////
//        mGLSurfaceView = new GLSurfaceView(this);
//        mGLSurfaceView.setEGLContextClientVersion(2);
//        mGLSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
//        mGLSurfaceView.setRenderer(new MyRenderer());
//        mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
//        setContentView(mGLSurfaceView);
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        mGLSurfaceView.onPause();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        mGLSurfaceView.onResume();
//    }
//
//    private static class MyRenderer implements GLSurfaceView.Renderer {
//
//        private static final String VERTEX_SHADER =
//                "attribute vec4 vPosition;\n"
//                        + "void main() {\n"
//                        + "  gl_Position = vPosition;\n"
//                        + "}";
//        private static final String FRAGMENT_SHADER =
//                "precision mediump float;\n"
//                        + "void main() {\n"
//                        + "  gl_FragColor = vec4(0.5, 0, 0, 1);\n"
//                        + "}";
//        private static final float[] VERTEX = {   // in counterclockwise order:
//                0, 1, 0,  // top
//                -0.5f, -1, 0,  // bottom left
//                1, -1, 0,  // bottom right
//        };
//
//        private final FloatBuffer mVertexBuffer;
//
//        private int mProgram;
//        private int mPositionHandle;
//
//        MyRenderer() {
//            mVertexBuffer = ByteBuffer.allocateDirect(VERTEX.length * 4)
//                    .order(ByteOrder.nativeOrder())
//                    .asFloatBuffer()
//                    .put(VERTEX);
//            mVertexBuffer.position(0);
//        }
//
//        static int loadShader(int type, String shaderCode) {
//            int shader = GLES20.glCreateShader(type);
//            GLES20.glShaderSource(shader, shaderCode);
//            GLES20.glCompileShader(shader);
//            return shader;
//        }
//
//        @Override
//        public void onSurfaceCreated(GL10 unused, EGLConfig config) {
//            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
//
//            mProgram = GLES20.glCreateProgram();
//            int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER);
//            int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER);
//            GLES20.glAttachShader(mProgram, vertexShader);
//            GLES20.glAttachShader(mProgram, fragmentShader);
//            GLES20.glLinkProgram(mProgram);
//
//            GLES20.glUseProgram(mProgram);
//
//            mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
//            Log.d("activity", "pos="+mPositionHandle);
//            GLES20.glEnableVertexAttribArray(mPositionHandle);
//            GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false,
//                    12, mVertexBuffer);
//        }
//
//        @Override
//        public void onSurfaceChanged(GL10 unused, int width, int height) {
//            GLES20.glViewport(0, 0, width, height);
//        }
//
//        @Override
//        public void onDrawFrame(GL10 unused) {
//            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
//
//            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
//        }
//    }
//
}


