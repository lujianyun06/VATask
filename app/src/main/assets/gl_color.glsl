precision mediump float;//精度 为float
varying vec2 v_texPo;//纹理位置  接收于vertex_shader
varying vec4 f_color;

uniform sampler2D sTexture;//纹理
void main() {
    vec4 tex2D;
    tex2D =texture2D(sTexture, v_texPo);
//    vec4 ff = vec4(0.5,0.0,0.0,0.0);
    gl_FragColor = f_color + tex2D;
}
