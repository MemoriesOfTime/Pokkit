package nl.rutgerkok.pokkit.boss;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import cn.nukkit.utils.BossBarColor;
import cn.nukkit.utils.DummyBossBar;
import nl.rutgerkok.pokkit.player.PokkitPlayer;

public class PokkitBossBar implements BossBar {
	
	ArrayList<cn.nukkit.utils.DummyBossBar> dummyBossBars = new ArrayList<>();
	BarColor color;
	BarStyle style;
	String title;
	float progressLength;
	private boolean visible = true;
	private final Set<BarFlag> flags = EnumSet.noneOf(BarFlag.class);

	public PokkitBossBar(String arg0, BarColor arg1, BarStyle arg2, BarFlag[] arg3) {
		setTitle(arg0);
		setColor(arg1);
		/*setStyle(arg2);
		for(int i = 0; i < arg3.length; i++)
		{
			addFlag(arg3[i]);
		}*/
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public void setTitle(String title) {
        for (DummyBossBar dummyBossBar : dummyBossBars) {
            dummyBossBar.setText(title);
        }
		this.title = title;
	}

	@Override
	public BarColor getColor() {
		return color;
	}
	
	public BossBarColor BarColorToBossBarColor(BarColor color) {
		switch(color)
		{
		case BLUE:
			return BossBarColor.BLUE; 
		case GREEN:
			return BossBarColor.GREEN;
		case PINK:
			return BossBarColor.PINK;
		case RED:
			return BossBarColor.RED;
		case WHITE:
			return BossBarColor.WHITE;
		case YELLOW:
			return BossBarColor.YELLOW;
		case PURPLE:
		default:
			return BossBarColor.PURPLE;
		}
	}

	@Override
	public void setColor(BarColor color) {
		this.color = color;
		BossBarColor bcolor = BarColorToBossBarColor(color);

        for (DummyBossBar dummyBossBar : dummyBossBars) {
            dummyBossBar.setColor(bcolor);
        }
	}

	@Override
	public BarStyle getStyle() {
		return style != null ? style : BarStyle.SOLID;
	}

	@Override
	public void setStyle(BarStyle style) {
		this.style = style;
	}

	@Override
	public void removeFlag(BarFlag flag) {
		flags.remove(flag);
	}

	@Override
	public void addFlag(BarFlag flag) {
		flags.add(flag);
	}

	@Override
	public boolean hasFlag(BarFlag flag) {
		return flags.contains(flag);
	}

	@Override
	public void setProgress(double progress) {
        for (DummyBossBar dummyBossBar : dummyBossBars) {
            dummyBossBar.setLength((float) progress * 100);
        }
		progressLength = (float) progress;
	}

	@Override
	public double getProgress() {
		return progressLength;
	}

	@Override
	public void addPlayer(Player player) {
		DummyBossBar build = new DummyBossBar.Builder(PokkitPlayer.toNukkit(player))
				.color(BarColorToBossBarColor(getColor()))
				.text(getTitle())
				.length((float) getProgress())
				.build();
		dummyBossBars.add(build);
		build.create();
	}

	@Override
	public void removePlayer(Player player) {
		for(int i = 0; i < dummyBossBars.size(); i++)
		{
			if(dummyBossBars.get(i).getPlayer().equals(PokkitPlayer.toNukkit(player)))
			{
				dummyBossBars.get(i).destroy();
				dummyBossBars.remove(i);
				i = dummyBossBars.size();
			}
		}
	}

	@Override
	public void removeAll() {
		for(int i = 0; i < dummyBossBars.size(); i++)
		{
			dummyBossBars.get(i).destroy();
			dummyBossBars.remove(i);
		}
	}

	@Override
	public List<Player> getPlayers() {
		ArrayList<Player> players = new ArrayList<>();
        for (DummyBossBar dummyBossBar : dummyBossBars) {
            Player p = PokkitPlayer.toBukkit(dummyBossBar.getPlayer());
            players.add(p);
        }
		return players;
	}

	@Override
	public void setVisible(boolean visible) {
		this.visible = visible;
		if (visible) {
			show();
		} else {
			hide();
		}
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	@Override
	public void show() {
        for (DummyBossBar dummyBossBar : dummyBossBars) {
            dummyBossBar.create();
        }
		
	}

	@Override
	public void hide() {
        for (DummyBossBar dummyBossBar : dummyBossBars) {
            dummyBossBar.destroy();
        }
	}
}
