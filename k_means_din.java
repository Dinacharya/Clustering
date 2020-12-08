import java.util.*;
import java.sql.*;
import java.text.ParseException;
import java.util.concurrent.TimeUnit;
import java.awt.Graphics;
import java.awt.*;
import javax.swing.JFrame;
import javax.swing.JPanel;
class k_means_db_version_with_plot_III extends JPanel{
	HashMap<Integer, recs> cluster1 = new HashMap<Integer, recs>();
		HashMap<Integer, recs> cluster2 = new HashMap<Integer, recs>();
		HashMap<Integer, recs> cluster3 = new HashMap<Integer, recs>();
		int i,counter =1;
		connection c = new connection();
		Statement stmt = c.getStatement();
		int j=0,k=0,l=0,flag=0;
		double tss=0; //total sum of squares
		recs cen1,cen2,cen3,newcen1,newcen2,newcen3;
		recs c1,c2,c3,grandmean; 
		recs mean1,mean2,mean3;
		ecludean_distance e;
		double dist1,dist2,dist3;
		variation_squared d;
	public k_means_db_version_with_plot_III()throws Exception
	{
		//String sql = "SELECT quantity,amount FROM `sales_data` WHERE 1";
		//String sql = "SELECT applicants/10,passed/10 FROM `applicant` where applicants >=30";
		String sql = "SELECT male/10 as male,female/10 as female FROM `malefemale` WHERE male>=10 and female>=10";
		d = new variation_squared();
		ResultSet rs = stmt.executeQuery(sql);
		rs.last();
		int rows = rs.getRow();
		rs.beforeFirst();
		recs[] ro = new recs[rows];
		for(i=0;i<rows;i++)
			ro[i] = new recs();
		i=0;
		e = new ecludean_distance();
		grandmean = new recs();
		grandmean.age = 0; grandmean.weight = 0;
		while(rs.next()){
			ro[i].set(rs.getInt(1),rs.getInt(2));
			grandmean.age += rs.getInt(1); grandmean.weight += rs.getInt(2);
			++i;
		}
		grandmean.age = grandmean.age/rows; grandmean.weight = grandmean.weight/rows;//mean of all data points
		
		i=0;
		while(i<rows){
			tss += d.distance(ro[i],grandmean);//total sum of squares 
			++i;
		}
		
		//cen1 = ro[10];cen2 = ro[24];cen3 = ro[251];//1st run
		//cen1 = ro[330];cen2 = ro[39];cen3 = ro[120];//2nd run
		//cen1 = ro[200];cen2 = ro[387];cen3 = ro[15];//3rd run & 1st run
		//cen1 = ro[300];cen2 = ro[787];cen3 = ro[2087];//2nd run
		//cen1 = ro[55];cen2 = ro[222];cen3 = ro[888];//3rd run
		//cen1 = ro[900];cen2 = ro[1800];cen3 = ro[2179];//4th run
		//cen1 = ro[0];cen2 = ro[2175];cen3 = ro[2134];//5th run
		
		//cen1 = ro[10];cen2 = ro[2224];cen3 = ro[4252];//1st run
		//cen1 = ro[300];cen2 = ro[2600];cen3 = ro[5200];//2nd run
		//cen1 = ro[1000];cen2 = ro[3550];cen3 = ro[5750];//3rd run
		//cen1 = ro[150];cen2 = ro[1777];cen3 = ro[2111];//4th run
		cen1 = ro[5940];cen2 = ro[477];cen3 = ro[3177];//5th run
		long startTime,endTime,durationInNano,durationInMillis;
		c1 = new recs();
		c2 = new recs();
		c3 = new recs();
		newcen1 = new recs();
		newcen2 = new recs();
		newcen3 = new recs();
		mean1 = new recs();mean2 = new recs();mean3 = new recs();
		mean1.age = 0;mean1.weight =0;
		mean2.age = 0;mean2.weight =0;
		mean3.age = 0;mean3.weight =0;
		startTime = System.nanoTime();
		while(counter < 100){
			cluster1.clear();cluster2.clear();cluster3.clear();
			j=0;k=0;l=0;					
			c1.age =0;c1.weight=0;
			c2.age =0;c2.weight=0;
			c3.age =0;c3.weight=0;	
			for(i=0;i<rows;i++){
				dist1 = e.distance(cen1,ro[i]);
				dist2 = e.distance(cen2,ro[i]);
				dist3 = e.distance(cen3,ro[i]);
				if(dist1<=dist2 && dist1<=dist3){
					cluster1.put(j,ro[i]);
					c1.age += ro[i].age;
					c1.weight += ro[i].weight; 
					j++;
				}else if(dist2<=dist3 && dist2<=dist1){
					cluster2.put(k,ro[i]);
					c2.age += ro[i].age;
					c2.weight += ro[i].weight; 
					k++;
				}else if(dist3<=dist1 && dist3<=dist2){
					cluster3.put(l,ro[i]);
					c3.age += ro[i].age;
					c3.weight += ro[i].weight; 
					l++;
				}
			}
			System.out.println("j: "+j+" k: "+k+" l: "+l);
					newcen1.age = c1.age/(j); newcen1.weight = c1.weight/(j);
					newcen2.age = c2.age/(k); newcen2.weight = c2.weight/(k);
					newcen3.age = c3.age/(l); newcen3.weight = c3.weight/(l);
				if((cen1.age == newcen1.age && cen1.weight == newcen1.weight) &&
			   (cen2.age == newcen2.age && cen2.weight == newcen2.weight) &&
			   (cen3.age == newcen3.age && cen3.weight == newcen3.weight)){
				   System.out.println("Done");
				   break;
			   }else{
					//assigning new cluster centers 
					cen1.age = newcen1.age;cen1.weight = newcen1.weight;
					cen2.age = newcen2.age;cen2.weight = newcen2.weight;
					cen3.age = newcen3.age;cen3.weight = newcen3.weight; 
			   }				
			++counter;
		if(counter == 50) break;
		}
		
		endTime = System.nanoTime();
		durationInNano = (endTime - startTime); 
		durationInMillis = TimeUnit.NANOSECONDS.toMillis(durationInNano); 
		System.out.println("K-Means");
		System.out.println("In NanoSeconds: "+durationInNano);
        System.out.println("In MilliSeconds: "+durationInMillis);  
		
			//calculating mean of each final cluster
				for(i=0;i<cluster1.size();i++){
					mean1.age += cluster1.get(i).age; mean1.weight += cluster1.get(i).weight;
				}
				mean1.age = mean1.age/cluster1.size(); mean1.weight = mean1.weight/cluster1.size();
				
				for(i=0;i<cluster2.size();i++){
					mean2.age += cluster2.get(i).age; mean2.weight += cluster2.get(i).weight;
				}
				mean2.age = mean2.age/cluster2.size(); mean2.weight = mean2.weight/cluster2.size();
				
				for(i=0;i<cluster3.size();i++){
					mean3.age += cluster3.get(i).age; mean3.weight += cluster3.get(i).weight;
				}
				mean3.age = mean3.age/cluster3.size(); mean3.weight = mean3.weight/cluster3.size();
		
		double ssb = 0;//sum of squares between groups
		ssb = d.distance(mean1,grandmean)*cluster1.size();
		ssb += d.distance(mean2,grandmean)*cluster2.size();
		ssb += d.distance(mean3,grandmean)*cluster3.size();

		//calculating the within groups of sum of squares (SSW)
		double ssw=0;
		for(i=0;i<cluster1.size();i++){
			ssw += d.distance(mean1,cluster1.get(i));
		}
		for(i=0;i<cluster2.size();i++){
			ssw += d.distance(mean2,cluster2.get(i));
		}
		for(i=0;i<cluster3.size();i++){
			ssw += d.distance(mean3,cluster3.get(i));
		}
		System.out.println("Number of iteration: "+counter);
		System.out.println("Centroid1: "+mean1.age+","+mean1.weight);
		System.out.println("Centroid2: "+mean2.age+","+mean2.weight);
		System.out.println("Centroid3: "+mean3.age+","+mean3.weight);
		System.out.println("Objects in first cluster: "+cluster1.size());
		System.out.println("Objects in second cluster: "+cluster2.size());
		System.out.println("Objects in third cluster: "+cluster3.size());
		System.out.println("Sum of squares between Groups: "+ssb);
		System.out.println("Total Sum of squares: "+tss+"="+(ssb+ssw));
		System.out.println("Sum of squares within Groups: "+ssw);
		
		System.out.println("Cluster 1");
		for(i=0;i<cluster1.size();i++){
			System.out.println("Age: "+cluster1.get(i).age+" weight: "+cluster1.get(i).weight);
		}
		System.out.println("Cluster 2");
		for(i=0;i<cluster2.size();i++){
			System.out.println("Age: "+cluster2.get(i).age+" weight: "+cluster2.get(i).weight);
		}
		System.out.println("Cluster 3");
		for(i=0;i<cluster3.size();i++){
			System.out.println("Age: "+cluster3.get(i).age+" weight: "+cluster3.get(i).weight);
		}
		
	}
	public void paint(Graphics g){
		for(i=0;i<cluster1.size();i++){
				g.setColor(Color.red);
				g.drawOval(cluster1.get(i).age,cluster1.get(i).weight,3,3);
			}
			//g.setColor(Color.black);
			//g.drawOval(cen1.age,cen1.weight,10,10);
		
		for(i=0;i<cluster2.size();i++){		
			g.setColor(Color.green);
			g.drawOval(cluster2.get(i).age,cluster2.get(i).weight,3,3);
			}
			//g.setColor(Color.black);
			//g.drawOval(cen2.age,cen2.weight,10,10);
		
		for(i=0;i<cluster3.size();i++){
			g.setColor(Color.yellow);
			g.drawOval(cluster3.get(i).age,cluster3.get(i).weight,3,3);
			}
			//g.setColor(Color.black);
			//g.drawOval(cen3.age,cen3.weight,10,10);
	}
	public static void main(String[] args) throws Exception{
		JFrame f = new JFrame();
		f.setSize(500,500);
		f.setVisible(true);
		f.setTitle("K-Means");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.add(new k_means_db_version_with_plot_III());
	}
}

class ecludean_distance{
	double dist;
	public double distance(recs cen1,recs r){
		double x1, y1, x2, y2;
		x1 = cen1.age; y1 = cen1.weight; x2 = r.age;y2 = r.weight;
		dist = Math.sqrt(Math.pow((x1-x2),2)+Math.pow((y1-y2),2));
		return dist; 
	}
}
class variation_squared{
	double dist;
	public double distance(recs cen1,recs r){
	int x1, y1, x2, y2;
		x1 = cen1.age; y1 = cen1.weight; x2 = r.age;y2 = r.weight;
		dist = Math.pow((x1-x2),2)+Math.pow((y1-y2),2);
		return dist; 
	}
}
/*
	System.out.println("New Centroid 1: "+String.format("%.2f",newcen1.age)+", "+String.format("%.2f",newcen1.weight));
/*for(i=0;i<rows;i++){
			System.out.println(ro[i].age+", "+ro[i].weight);
		}
			*/
			
			/**/
/*System.out.println("WCSS="+wcss+" Number of elements in clusters: "+cluster1.size()+", "+cluster2.size()+", "+cluster3.size());	   */

/*	double cluster_cen_dist12,cluster_cen_dist13,cluster_cen_dist23;
wcss = (wcss1+wcss2+wcss3);
		/*cluster_cen_dist12 = e.distance(cen1,cen2);
		cluster_cen_dist13 = e.distance(cen1,cen3);
		cluster_cen_dist23 = e.distance(cen2,cen2);
		//double bcss = (cluster_cen_dist12+cluster_cen_dist13+cluster_cen_dist23)/3;
		double bcss = tss - wcss;
		System.out.println("Average BCSS: "+bcss);
		System.out.println("Variation: "+(bcss/tss));
		System.out.println("Data Variation: "+(wcss/tss));*/
		
		/*System.out.println("Old Centroid 1: "+cen1.age+", "+cen1.weight);
			System.out.println("Old Centroid 2: "+cen2.age+", "+cen2.weight);
			System.out.println("Old Centroid 3: "+cen3.age+", "+cen3.weight);
			
			System.out.println("New Centroid 1: "+newcen1.age+", "+newcen1.weight);
					System.out.println("New Centroid 2: "+newcen2.age+", "+newcen2.weight);
					System.out.println("New Centroid 3: "+newcen3.age+", "+newcen3.weight);
			
			*/
			
			
			//calculating Total Sum of squares -> total variation
		/*double sst = 0;
		for(i=0;i<cluster1.size();i++){
			sst += e.distance(cluster1.get(i),grandmean);
		}
		for(i=0;i<cluster2.size();i++){
			sst += e.distance(cluster2.get(i),grandmean);
		}
		for(i=0;i<cluster3.size();i++){
			sst += e.distance(cluster3.get(i),grandmean);
		}
		*/
		//calculating the between groups sum of squares (SSG)
		/*
		System.out.println("Number of elements in clusters1: "+cluster1.size()+"; WCSS: "+(wcss1/cluster1.size()));
		System.out.println("Number of elements in clusters2: "+cluster2.size()+"; WCSS: "+(wcss2/cluster2.size()));
		System.out.println("Number of elements in clusters3: "+cluster3.size()+"; WCSS: "+(wcss3/cluster3.size()));
		*/
		