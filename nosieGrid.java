void setup(){
  size(700,700);
}
int cubeSize = 20;
void cubes(){
  for(int i=0;i<700;i+=cubeSize){
    for(int j=0;j<700;j+=cubeSize){
      noFill();
      stroke(10);
      rect(i,j,cubeSize,cubeSize);
    }
  }
}
float conX = 232;
float conY = 122;
void draw() {
  background(50);
  //frameRate(1);
  fill(0,200,50);
  float x = map(noise(conX),0,1,0,width);
  float y = map(noise(conY),0,1,0,width);
  //circle(x,y,10);
  rect(x-(x%cubeSize),y-(y%cubeSize),cubeSize,cubeSize);
  conX+=0.01;
  conY+=0.01;
  cubes();
}
