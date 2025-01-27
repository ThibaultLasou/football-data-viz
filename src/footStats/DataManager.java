package footStats;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class DataManager 
{
	static String[] cheminsFichiers = {"res/2013-11-03_tromso_stromsgodset_first.csv", "res/2013-11-03_tromso_stromsgodset_second.csv", "res/2013-11-07_tromso_anji_first.csv", "res/2013-11-07_tromso_anji_second.csv"};
	private ArrayList<JoueurStat> listeJoueurs;
	private ArrayList<StatsTemps> listeTemps;
	
	public DataManager()
	{
		listeJoueurs = new ArrayList<JoueurStat>();
		listeTemps = new ArrayList<StatsTemps>();
	}
	
	public void lireFichier(int index)
	{
		File fichier = new File(cheminsFichiers[index]);
		FileReader fr = null;
		try
		{
			fr = new FileReader(fichier);
			BufferedReader br = new BufferedReader(fr);
			try
			{
				String ligne = br.readLine();
				while(ligne != null)
				{
					//i++;
					//System.out.println(i);
					String[] tab = ligne.split(",");
					addStats(tab);
					ligne = br.readLine();
				}
				br.close();
				fr.close();
			}
			catch(IOException e)
			{
				System.out.println("Erreur de lecture : "+e.getMessage());
			}
		}
		catch(FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}
	
	public ArrayList<JoueurStat> getListeJoueur()
	{
		return (ArrayList<JoueurStat>) listeJoueurs.clone();
	}
	
	public StatsTempsJoueur getEnreg(int index) throws ArrayIndexOutOfBoundsException
	{
		for(StatsTemps t : this.listeTemps)
		{
			if(index > t.listeStatsTJ.size())
			{
				index -= t.listeStatsTJ.size();
			}
			else
			{
				return(t.listeStatsTJ.get(index));
			}
		}
		throw new ArrayIndexOutOfBoundsException();
	}
	
	public StatsTemps getEnregT(int index) throws ArrayIndexOutOfBoundsException
	{
		if(index<this.listeTemps.size())
		{
			return this.listeTemps.get(index);	
		}
		else
		{
			throw new ArrayIndexOutOfBoundsException();
		}
	}
	
	public void addStats(String[] tab)
	{
		String n = tab[0].substring(1, tab[0].length()-1);
		Date timestamp = parseDate(n);
		StatsTempsJoueur stat = new StatsTempsJoueur(timestamp, tab);

		
		if(listeTemps.size() != 0 && listeTemps.get(listeTemps.size()-1).temps.equals(timestamp))
		{
			listeTemps.get(listeTemps.size()-1).listeStatsTJ.add(stat);
		}
		else
		{	
			StatsTemps nouveau = new StatsTemps(timestamp);
			nouveau.listeStatsTJ.add(stat);
			listeTemps.add(nouveau);
		}
		JoueurStat j;
		try 
		{
			j = getJStatsTot(stat.tag_id);
		} 
		catch (NoPlayerException e) 
		{
			j = new JoueurStat(stat.tag_id);
			this.listeJoueurs.add(j);
		}
		if(stat.pos_x >= 0 && stat.pos_y >= 0 && stat.pos_x < Parcelle.LONGUEUR && stat.pos_y < Parcelle.LARGEUR)
		{
					j.passage((int)stat.pos_x, (int)stat.pos_y);
		}
	}
	
	public Date parseDate(String s)
	{
		int t = s.length();
		int vt = "yyyy-MM-dd hh:mm:ss.SS".length();
		if(t != vt)
		{
			if(vt - t == 1)
			{
				s += "0";
			}
			else
			{
				s += ".00";		
			}
		}
		Date d = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SS");
		try 
		{
			d = df.parse(s);
		}
		catch(ParseException e)
		{
			e.printStackTrace();
		}
		return d;
	}
	
	@Override
	public String toString() 
	{
		return "Terrain [cheminsFichiers=" + Arrays.toString(cheminsFichiers)
				+ "\nlisteJoueurs=" + listeJoueurs + "\nlisteTemps="
				+ listeTemps + "]";
	}

	/*public static void main(String[] args)
	{
		DataManager e = new DataManager();
		e.lireFichier(0);
		for(StatsTemps s : e.listeTemps)
		{
			System.out.println(s);
		}
	}*/

	public int getRecordTNumber() 
	{
		return listeTemps.size();
	}
	
	public JoueurStat getJStatsTot(int playerID) throws NoPlayerException
	{
		for(JoueurStat j : this.listeJoueurs)
		{
			if(j.getID() == playerID)
			{
				return j;
			}
		}
		throw new NoPlayerException();
	}
}

