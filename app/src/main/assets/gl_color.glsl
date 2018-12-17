#ifdef GL_FRAGMENT_PRECISION_HIGH
precision highp float;
#else
precision mediump float;
#endif
varying vec4 vColor;

void main() {
    gl_FragColor = vColor;
//    gl_FragColor = vec4(1.0f);
}
