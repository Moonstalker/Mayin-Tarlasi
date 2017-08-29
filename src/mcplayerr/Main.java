package mcplayerr;

import java.util.HashMap;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import net.md_5.bungee.api.ChatColor;

public class Main extends JavaPlugin implements Listener{
	
	HashMap<String, Integer> hashint = new HashMap<String, Integer>();
	HashMap<String, World> hashworld = new HashMap<String, World>();
	
	@Override
	public void onEnable() {Bukkit.getPluginManager().registerEvents(this, this);}
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("mt")) {
			if(sender instanceof Player) {
				Player player = (Player) sender;
				
				
				if(args.length==0) {
					sender.sendMessage(ChatColor.GREEN+"Mayın Tarlası Minecraftplayerr tarafından kodlanmıştır.");
					sender.sendMessage(ChatColor.GREEN+"Mayın Tarlası komutları:");
					sender.sendMessage(ChatColor.GREEN+"/mt başlat [zorluk] (Yeni bir mayın tarlası başlatır, eskisi silinir.)");
					sender.sendMessage(ChatColor.GREEN+"Zorluk 1 ila 999999999 arasında olabilir.");
					sender.sendMessage(ChatColor.GREEN+"1: İmkansız, 999999999: Çok Basit");
					sender.sendMessage(ChatColor.GREEN+"\nVarsayılan zorluk:2 (%50 mayın)");
				}
				
				else if(args[0].equalsIgnoreCase("başlat")) {
					
					if(!hashint.containsKey("ayar")) player.sendMessage(ChatColor.RED+"İlk önce /mt ayarla ile konum ayarlayın.");
					
					else {
						
						World world = (World) hashworld.get("world");
						int locx = (int) hashint.get("x");
						int locy = (int) hashint.get("y");
						int locz = (int) hashint.get("z");
						
						if(args.length==1) {
							player.sendMessage(ChatColor.GREEN+"Mayın tarlası varsayılan zorluk (2) ile başlatıldı.");
							for (int x = 0; x < 9; x++) {for (int y = 0; y < 9; y++) {
								setblock(world, locx+x, locy+y, locz, 1);
								hashint.put(""+world+(locx+x)+(locy+y)+locz, randInt(0,1));
							}}
						}
						else if(isInt(args[1]) && args.length == 2) {
							player.sendMessage(ChatColor.GREEN+"Mayın tarlası girdiğiniz zorlukla başlatıldı: "+Integer.parseInt(args[1]));
							for (int x = 0; x < 9; x++) {for (int y = 0; y < 9; y++) {
								setblock(world, locx+x, locy+y, locz, 1);
								if(randInt(1,Integer.parseInt(args[1]))==1)
									hashint.put(""+world+(locx+x)+(locy+y)+locz, 1);
								else 
									hashint.put(""+world+(locx+x)+(locy+y)+locz, 0);
							}}
						}else player.sendMessage(ChatColor.LIGHT_PURPLE+"Zorluk sayısı rakamla yazılmalıdır (maks 999999999)");
						
					}

				}else if(args[0].equalsIgnoreCase("ayarla")) {
					Location playerloc = player.getLocation();
					hashint.clear();
					hashworld.clear();
					
					hashint.put("ayar", 1);
					hashworld.put("world", player.getWorld());
					hashint.put("x", (int) playerloc.getBlockX());
					hashint.put("y", (int) playerloc.getBlockY());
					hashint.put("z", (int) playerloc.getBlockZ());
					
					player.sendMessage(ChatColor.GREEN+"Mayın tarlası lokasyonu ayarlandı. ");
					
				}
			}
			return true;
		}
		return false; 
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		World world = player.getWorld();
		Block block = event.getBlock();
		int x = block.getX();
		int y = block.getY();
		int z = block.getZ();
		
		if(hashint.containsKey(""+world+x+y+z)) {
			int locx = (int) hashint.get("x");
			int locy = (int) hashint.get("y");
			int locz = (int) hashint.get("z");
			if(block.getType()==Material.STONE) {
				event.setCancelled(true);
				if(hashint.get(""+world+x+y+z)==1) {
					player.sendMessage(ChatColor.LIGHT_PURPLE+"  Olamaz, mayın!");
					new BukkitRunnable(){
						int a=0;
						Location blockloc = block.getLocation();
						@SuppressWarnings("deprecation")
						@Override
			            public void run(){
							for (int x = 0; x < 9; x++) 
								for (int y = 0; y < 9; y++) {
									blockloc = new Location(world, locx+x ,locy+y ,locz);
									world.playEffect(blockloc,Effect.EXPLOSION_HUGE,0);
								}
						    a++;
						    if(a>13){
						    	for (int x = 0; x < 9; x++) for (int y = 0; y < 9; y++) {
						    		if(randInt(0,3)==0)
						    			setblock(world, locx+x, locy+y, locz, 4);
						    		else 
						    			setblock(world, locx+x, locy+y, locz, 0);
						    	}
						    	
						    	
						    	player.sendMessage(ChatColor.LIGHT_PURPLE+"    Yeniden oynamak için /mt başlat");this.cancel();return;
						    }
						}
					}.runTaskTimer(this, 5, 5);
				}else {
					setblock(world,x,y,z, 35);
				}
			}
		}
	}
	
	public static int randInt(int min, int max) {Random rand=new Random();int randomNum=rand.nextInt((max - min) + 1) + min;
	    return randomNum;
	}
	public static boolean isInt(String s) {
	    try {
	        Integer.parseInt(s);
	    } catch (NumberFormatException nfe) {
	        return false;
	    }
	    return true;
	}
	@SuppressWarnings("deprecation")
	public void setblock(World world, int x, int y, int z, int id){
		Location loc = new Location(world, x ,y ,z);
		Block block = loc.getBlock();
		block.setTypeId(id);
	}

}
