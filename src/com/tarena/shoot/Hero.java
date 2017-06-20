package com.tarena.shoot;
import java.awt.image.BufferedImage;
import java.util.Random;
public class Hero extends FlyingObject{
private int life;//命
private int doubleFire;//火力值
private BufferedImage[] images;
private int index;
public Hero(){
	image = ShootGame.hero0;
	width = image.getWidth();
	height = image.getHeight();
	Random rand = new Random();
	x = 150;
	y = 400;
	life = 3;
	doubleFire = 0;
	images = new BufferedImage[]{ShootGame.hero0,ShootGame.hero1};
	index = 0;
}
public void step(){
	image = images[index++/10%images.length];
	  /*
	  index++;
	  int a = index/10;
	  int b = a%2;
	  image = images[b];
	  */
}
public Bullet[] shoot(){
	int xstep = this.width/4;
	int ystep = 20;
	if(doubleFire>0){//双倍火力
		Bullet[] bs = new Bullet[2];
		bs[0] = new Bullet(this.x+1*xstep,this.y-ystep);
		bs[1] = new Bullet(this.x+3*xstep,this.y-ystep);
		doubleFire-=2;
		return bs;
	}else{//单倍火力
		Bullet[] bs = new Bullet[1];
		bs[0] = new Bullet(this.x+2*xstep,this.y-ystep);
		return bs;
	}
}
public void moveTo(int x,int y){
	this.x = x - this.width/2;
	this.y = y - this.height/2;
}
public boolean outofBounds(){
	  return false;
}
public void addLife(){
	life++;
}
public int getLife(){
	return life;
}
public void subtractLife(){
	life--;
}
public void addDoubleFire(){
	doubleFire+=40;
}
public void clearDoubleFire(){
	doubleFire=0;
}
public boolean hit(FlyingObject obj){
	int x1=obj.x-this.width/2;
	int x2=obj.x+obj.width+this.width/2;
	int y1=obj.y-this.height/2;
	int y2=obj.y+obj.width+this.width/2;
	int x=this.x+this.width/2;
	int y=this.y+this.height/2;
	return x>x1 && x<x2
			&& 
			 y>x1 && y<y2;
}
}



















