import java.util.*;
import java.sql.*;
import java.io.*;
//tuple is the same as pair in C# 
class Tuple {
	//make each transaction it in c or l has two value the itemset and its support 
	Set<Integer> itemset;
	int support;
	Tuple() {
		itemset = new HashSet<>();
		support = -1;
	}
	Tuple(Set<Integer> s) {
		itemset = s;
		support = -1;
	}
	Tuple(Set<Integer> s, int i) {
		itemset = s;
		support = i;
	}
}
public class Main
{
	static Set<Tuple> c;
	static Set<Tuple> l;
	static int d[][];
	static int min_support;
	public static void main(String[] args) throws Exception{
	    getDatabase();
		c = new HashSet<>();
		l = new HashSet<>();
		int m, n;
		System.out.println("Enter the minimum support :");
		//read the minimum support 
		BufferedReader read=new BufferedReader(new InputStreamReader(System.in));
        min_support =Integer.parseInt(read.readLine());
        
		Set<Integer> candidate_set = new HashSet<>();
		//print transactions with items in each one
		//d.length means total number of transations 
		for(int i=0 ; i < d.length ; i++) {
			System.out.println("Transaction Number: " + (i+1) + ":");
			//d[i].length means the number of items per transaction 
			for(int j=0 ; j < d[i].length ; j++) {
				//print each item 
				System.out.print("Item number " + (j+1) + " = ");
				System.out.println(d[i][j]);
				//add this item to candidate_set
				candidate_set.add(d[i][j]);
			}
		}
		//loop in each item in the candidate_set
		Iterator<Integer> iterator = candidate_set.iterator();
		while(iterator.hasNext()) {
			Set<Integer> s = new HashSet<>();
			//add the item to the temp set 
			s.add(iterator.next());
			//create new tuple and add the temp and support which get from count function
			Tuple t = new Tuple(s, count(s));
			//add tuple to the main candidate set 
			c.add(t);
		}
		prune();
		FrequentItemsets();
	}
	static int count(Set<Integer> s) {
		//intialize support 
		int support = 0;
		int count;
		//boolean variable to break the loop 
		boolean containsElement;
		//d.length means total number of transactions 
		for(int i=0 ; i < d.length ; i++) {
			count = 0;
			//iterate on the set s 
			Iterator<Integer> iterator = s.iterator();
			while(iterator.hasNext()) {
				//temp variable to save current element in set 
				int element = iterator.next();
				//intialize boolean variable with false
				containsElement = false;
				//loop in each transaction 
				for(int k=0 ; k < d[i].length ; k++) {
					//check if this element = the current element
					if(element == d[i][k]) {
						containsElement = true;
						//increase support count 
						count++;
						break;
					}
				}
				if(!containsElement) {
					break;
				}
			}
			//if support count = size of set 
			if(count == s.size()) {
				//increase item support 
				support++;
			}
		}
		return support;
	}
	
	static void prune() {
		//clear item list
		l.clear();
		//create iterator to loop in the candidate list
		Iterator<Tuple> iterator = c.iterator();
		while(iterator.hasNext()) {
			Tuple t = iterator.next();
			//if support of l > min support add t to the list 
			if(t.support >= min_support) {
				l.add(t);
			}
		}
		//print list 
		System.out.println(" frequent list ");
		// loop in list to print list items with its support 
		for(Tuple t : l) {
			System.out.println(t.itemset + " : " + t.support);
		}
	}
	
	static void FrequentItemsets() {
		
		boolean toBeContinued = true;
		int element = 0;
		int size = 1;
		
		Set<Set> candidate_set = new HashSet<>();
		while(toBeContinued) {
			candidate_set.clear();
			//clear main candidate list 
			c.clear();
			//create iterator on the item list 
			Iterator<Tuple> iterator = l.iterator();
			while(iterator.hasNext()) {
				//create temp tuple carry current element at l (frequent item list)
				Tuple t1 = iterator.next();
				//take the itemlist from the tuple and assign it to temp set 
				Set<Integer> temp = t1.itemset;
				Iterator<Tuple> it2 = l.iterator();
				while(it2.hasNext()) {
					//create another temp tuple carry current element at l (frequent item list)
					Tuple t2 = it2.next();
					//
					Iterator<Integer> it3 = t2.itemset.iterator();
					while(it3.hasNext()) {
						try {
							//intialize element with the value of the iterator
							element = it3.next();
						} catch(ConcurrentModificationException e) {
							break;
						}
						//add element set  
						temp.add(element);
						if(temp.size() != size) {
							//creat array to copy temp set in it 
							Integer[] int_arr = temp.toArray(new Integer[0]);
							//create another temp set to take the previous temp value 
							Set<Integer> temp2 = new HashSet<>();
							//copy elements from array to temp2
							for(Integer x : int_arr) {
								temp2.add(x);
							}
							//add temp to the candidate_set
							candidate_set.add(temp2);
							//remove element from temp1
							temp.remove(element);
						}
					}
				}
			}
			Iterator<Set> candidate_set_iterator = candidate_set.iterator();
			while(candidate_set_iterator.hasNext()) {
				Set s = candidate_set_iterator.next();
				c.add(new Tuple(s, count(s)));
			}
			prune();
			if(l.size() <= 1) {
				toBeContinued = false;
			}
			size++;
		}
		//print final frequent item set 
		System.out.println("\n FINAL Freqent List ");
		for(Tuple t : l) {
			System.out.println(t.itemset + " : " + t.support);
		}
	}
	
	static void getDatabase() throws Exception {
		//buffer reader to read user iput
	    BufferedReader b=new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter the number of transaction :");
		//read number of transactions
        int numoftrans=Integer.parseInt(b.readLine());
		
        System.out.println("Enter the number of objects per transaction :");
		//read number of object per transaction
        int numofobject=Integer.parseInt(b.readLine());
		// 2 arrays to store transaction id at arr1 and item at arr2
        int arr1[]=new int[numoftrans];
        int arr2[]=new int[numofobject];
		//hash map to store transaction id as key and list of items at this transaction as value
        Map<Integer, List <Integer>> m = new HashMap<>();
		//temp list to store items 
		List<Integer> temp;
		//loop to fill the 2 arrays and temp list then fill the hash map
        for(int i=0;i<numoftrans;i++){
            System.out.println("Transaction "+(i+1)+" :");
            arr1[i]=i+1;
            temp = new LinkedList<>();
            for(int j=0;j<numofobject;j++){
                System.out.println("Object "+(j+1)+" =");
                arr2[j]=Integer.parseInt(b.readLine()); 
            	temp.add(arr2[j]);
            }
			
			m.put(arr1[i], temp);
        }
        //create new set contains transaction ids
		Set<Integer> keyset = m.keySet();
		//initialize d
		d = new int[keyset.size()][];
		//create iterator to loop in the set of transaction ids
		Iterator<Integer> iterator = keyset.iterator();
		int count = 0;
		//loop 
		while(iterator.hasNext()) {
			temp = m.get(iterator.next());
			Integer[] int_arr = temp.toArray(new Integer[0]);
			d[count] = new int[int_arr.length];
			for(int i=0 ; i < d[count].length ; i++) {
				d[count][i] = int_arr[i].intValue();
			}
			count++;
		}
	}
}

