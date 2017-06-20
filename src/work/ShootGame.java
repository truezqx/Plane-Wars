package work;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import java.awt.Graphics;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Arrays;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Color;
import java.awt.Font;
public class ShootGame extends JPanel{
	public static final int WIDTH =400;
	public static final int HEIGHT = 654;
   public static BufferedImage background;
   public static BufferedImage start;
   public static BufferedImage pause;
   public static BufferedImage gameover;
   public static BufferedImage airplane;
   public static BufferedImage bee;
   public static BufferedImage hero0;
   public static BufferedImage hero1;
   public static BufferedImage bullet;
   public static final int START = 0;
   public static final int RUNNING = 1;
   public static final int  PAUSE= 2;
   public static final int GAME_OVER =3;
   private int state = START;
   
   private Hero hero =new Hero();
   private FlyingObject[] flyings ={};
   private Bullet[] bullets ={};
   
   static{//初始化静态资源（图片）
	   try{
		   background = ImageIO.read(ShootGame.class.getResource("background.png"));
		   start      = ImageIO.read(ShootGame.class.getResource("start.png"));
		   pause      = ImageIO.read(ShootGame.class.getResource("pause.png"));
		   gameover   = ImageIO.read(ShootGame.class.getResource("gameover.png"));
		   airplane   = ImageIO.read(ShootGame.class.getResource("airplane.png"));
		   bee        = ImageIO.read(ShootGame.class.getResource("bee.png"));
		   bullet     = ImageIO.read(ShootGame.class.getResource("bullet.png"));
		   hero0      = ImageIO.read(ShootGame.class.getResource("hero0.png"));
		   hero1      = ImageIO.read(ShootGame.class.getResource("hero1.png"));
		   
	   }catch(Exception e){
		   e.printStackTrace();
	   }
   }
   public FlyingObject nextOne(){
	   Random rand = new Random();
	   int type = rand.nextInt(20);
	   if(type==0){//为0是为蜜蜂
		   return new Bee();
	   }else{//否则为敌机
		   return new Airplane();
	   }
   }
   int flyEnteredIndex =0;
   public void enteraction(){
	   flyEnteredIndex ++;
	   if(flyEnteredIndex%40==0){//40*10毫秒
		   FlyingObject one = nextOne();//获取一个敌人对象
		   flyings =Arrays.copyOf(flyings, flyings.length+1);//扩容
		   flyings[flyings.length-1] = one;//将该对象添加到敌人数组的最后
	   }
   }
   public void stepaction(){
	   hero.step();//调用step
	   for(int i=0;i<flyings.length;i++){//遍历敌人数组
		    flyings[i].step();//调用step
	   }
	   for(int i=0;i<bullets.length;i++){//遍历子弹数组
		   bullets[i].step();//调用step
	   }
   }
   int shootindex = 0;
   public void shootaction(){
	   shootindex++;
	   if(shootindex%30==0){
		    Bullet[] bs = hero.shoot();
		    bullets = Arrays.copyOf(bullets, bullets.length+bs.length);
		    System.arraycopy(bs, 0, bullets, bullets.length-bs.length, bs.length);
	   }
   }
   public void outOfBoundaction(){
	   int index = 0;
	   FlyingObject[] flyingLives = new FlyingObject[flyings.length];
	   for(int i=0;i<flyings.length;i++){
		   FlyingObject f = flyings[i];
		   if(!f.outOfBounds()){
			   flyingLives[index] = f;
			   index++;
		   }
	   }
	   flyings = Arrays.copyOf(flyingLives, index);
	   index=0;
	   Bullet[] bulletLives = new Bullet[bullets.length];
	   for(int i=0;i<bullets.length;i++){
		   Bullet b = bullets[i];
		   if(!b.outOfBounds()){
			   bulletLives[index] = b;
			   index++;
		   }
	   }
	   bullets = Arrays.copyOf(bulletLives, index);
   }
   public void bangAction(){
	   for(int i=0;i<bullets.length;i++){
		   Bullet b = bullets[i];
		   bang(b);
	   }
   }
   int score =0;
   public void bang(Bullet b){
	   int index =-1;
	   for(int i=0;i<flyings.length;i++){
		   FlyingObject f = flyings[i];
		   if(f.shootBy(b)){
			   index = i;
			   break;
		   }
	   }
	   if(index!=-1){
		   FlyingObject one = flyings[index];
		   if(one instanceof Enemy){
			   Enemy e = (Enemy)one;
			   score+=e.getScore();
		   }
		   if(one instanceof Award){
			   Award a = (Award)one;
			   int type = a.getType();
			   switch(type){
			   case Award.DOUBLE_FIRE:
				   hero.addDoubleFire();
				   break;
			   case Award.LIFE:
				   hero.addLife();
				   break;
			   }
		   }
		   FlyingObject t = flyings[index];
		   flyings[index] = flyings[flyings.length-1];
		   flyings[flyings.length-1]=t;
		   flyings = Arrays.copyOf(flyings, flyings.length-1);
	   }
   }
   public void checkGameOverAction(){
	   if(isGameOver()){
		   state = GAME_OVER;
	   }
   }
   public boolean isGameOver(){
	   for(int i=0;i<flyings.length;i++){
		   FlyingObject f = flyings[i];
		   if(hero.hit(f)){
			   hero.substractLife();
			   hero.clearDoubleFire();
			   FlyingObject t = flyings[i];
			   flyings[i] = flyings[flyings.length-1];
			   flyings[flyings.length-1] = t;
			   flyings = Arrays.copyOf(flyings, flyings.length-1);
		   }
	   }
	   return hero.getLife()<=0;
   }
   public void action(){
	   MouseAdapter l =new MouseAdapter(){
		 public void mouseMoved(MouseEvent e){
			 if(state==RUNNING){
			 int x = e . getX();
			 int y = e . getY();
			 hero.moveTo(x, y);
			 }
		 }
		 public void mouseClicked(MouseEvent e){
			switch(state){
			case START:
				state = RUNNING;
				break;
			case GAME_OVER:
				score=0;
				hero = new Hero();
				bullets = new Bullet[0];
				flyings = new FlyingObject[0];
				state = START;
				break;
			}
		 }
		 public void mouseExited(MouseEvent e){
			 if(state==RUNNING){
				 state = PAUSE;
			 }
		 }
		 public void mouseEntered(MouseEvent e){
			 if(state==PAUSE){
				 state=RUNNING;
			 }
		 }
	   };
	   this.addMouseListener(l);
	   this.addMouseMotionListener(l);
	   Timer timer = new Timer();
	   int intervel = 10;//时间间隔为10毫秒
	   timer.schedule(new TimerTask(){
		   public void run(){
			   if(state==RUNNING){
			   enteraction();//敌人入场
			   stepaction();
			   shootaction();
			   outOfBoundaction();
			   bangAction();
			   checkGameOverAction();
			   }
			   repaint();
		   }
	   },intervel,intervel);
   }
   public void paint(Graphics g){
	   g.drawImage(background, 0, 0, null);
	   paintHero(g);
	   paintFlyingObjects(g);
	   paintBullets(g);
	   paintScoreAndLife(g);
	   paintState(g);
   }
   public void paintState(Graphics g){
	   switch(state){
	   case START:
		   g.drawImage(start,0,0,null);
		   break;
	   case PAUSE:
		   g.drawImage(pause,0,0,null);
		   break;
	   case GAME_OVER:
		   g.drawImage(gameover,0,0,null);
		   break;
	   }
   }
   public void  paintScoreAndLife(Graphics g){
	   g.setColor(new Color(0xff0000));
	   g.setFont(new Font(Font.SANS_SERIF,Font.BOLD,24));
	   g.drawString("SCORE:"+score,10 , 25);
	   g.drawString("LIFE:"+hero.getLife(),10 , 45);
   }
   public void paintHero(Graphics g){
	   g.drawImage(hero.image, hero.x, hero.y, null);
   }
   public void paintFlyingObjects(Graphics g){
	   for(int i=0;i<flyings.length;i++){
		   FlyingObject f = flyings[i];
		   g.drawImage(f.image, f.x, f.y, null);
	   }
   }
   public void paintBullets(Graphics g){
	   for(int i=0;i<bullets.length;i++){
		   Bullet b =bullets[i];
		   g.drawImage(b.image, b.x, b.y, null);
	   }
   }
	public static void main(String[] args) {
		JFrame frame = new JFrame("FLY");
	     ShootGame game = new ShootGame();
	     frame.add(game);
	     
	     frame.setSize(WIDTH, HEIGHT);
	     frame.setAlwaysOnTop(true);
	     frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	     frame.setLocationRelativeTo(null);
	     frame.setVisible(true);
	     game.action();
	}

}
