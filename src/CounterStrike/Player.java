package CounterStrike;

import java.io.Serializable;

public class Player implements Serializable {

	private static final long serialVersionUID = 1L;
	private int hp, x, y, face, bullets;
	private boolean dead;

	// face: 4 = left, 2 = down, 6 = right, 8 = up

	public Player(int x, int y) {
		super();
		this.x = x;
		this.y = y;
		// set default hp to 3
		setHp(3);
		// set default face to right
		setFace(6);
		dead = false;
	}

	public int getBullets() {
		return bullets;
	}

	public void decBullets() {
		bullets--;
	}

	public void setBullets(int bullets) {
		this.bullets = bullets;
	}

	public void die() {
		dead = true;
	}

	public void respawn() {
		dead = false;
	}

	public boolean isDead() {
		return dead;
	}

	public int getHp() {
		return hp;
	}

	public void setHp(int hp) {
		this.hp = hp;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getFace() {
		return face;
	}

	public void setFace(int face) {
		this.face = face;
	}

}
