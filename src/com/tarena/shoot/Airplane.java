package com.tarena.shoot;
import java.util.Random;
public class Airplane extends FlyingObject implements Enemy{
	private int speed = 2 ;//走得步数
	public Airplane(){
		image = ShootGame.airplane;
		width = image.getWidth();
		height = image.getHeight();
		Random rand = new Random();
		x = rand.nextInt(ShootGame.WIDTH-this.width);
		y =-this.height;
	}
  public int getScore(){
	  return 5;
  }
  public void step(){
	  y+= speed;
  }
  public boolean outofBounds(){
	  return this.y>=ShootGame.HEIGHT;
  }
}
