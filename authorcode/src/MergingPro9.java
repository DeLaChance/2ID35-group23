import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

/*
 * 本类从文件中读取label信息，不用每次都重新label，节省下label的时间
 */
public class MergingPro9 extends LabelGraphFromFile{

	Integer[] COM;
	byte[] array; //用以快速把第三部分的点的label中的COM元素抛掉
	Table0 table; //从primitive 到 duplicated 的map
	int dup_cid = 0; //duplicated cid
	int interval_start = 0;
	//ArrayList<Integer> AllCNode; //从duplicated 到 primitive 的map
	int[] AllCNode;
	int AllCNode_index = 0;

	int scaningfactor = -1;
	int start_of_third = -1;
	
	public MergingPro9(File xmlFile, int scaningfactor_in, int start_of_third_in) {
		
		super(xmlFile);
		
		scaningfactor = scaningfactor_in;
		start_of_third = start_of_third_in;
		
		System.out.println("labeling start...");
		long time1 = System.currentTimeMillis();
		this.label(scaningfactor,start_of_third);	
		long time2 = System.currentTimeMillis();
		System.out.println("file label use "+(time2-time1));
	}
	
	//用以duplicate第一第二部分
	public void duplication(DataVertex curVertex){
		
		//第一第二部分全zip过了，这里就全部unzip
		//Huffman.unzip_CNeighbor(curVertex);

		//开始duplication
		curVertex.pos_of_cons_Ci = new int[curVertex.CNeighbor_length];
		curVertex.linked = new byte[curVertex.CNeighbor_length];
		curVertex.Fixed = new int[curVertex.CNeighbor_length];
		curVertex.Free = new int[curVertex.CNeighbor_length];
		for(int j=0; j<curVertex.CNeighbor_length; j++){
			curVertex.pos_of_cons_Ci[j] = -1;
			curVertex.linked[j] = 0;
		}
	}
	

	//用以duplicate第三部分，因为第三部分是没有压缩过的，所以不用解压
	public void duplication3(DataVertex curVertex){
		
		curVertex.CNeighbor_length = curVertex.CNeighbor.length;
		curVertex.THIRD = true;
		
		//开始duplication
		curVertex.pos_of_cons_Ci = new int[curVertex.CNeighbor_length];
		curVertex.linked = new byte[curVertex.CNeighbor_length];
		curVertex.Fixed = new int[curVertex.CNeighbor_length];
		curVertex.Free = new int[curVertex.CNeighbor_length];
		for(int j=0; j<curVertex.CNeighbor_length; j++){
			curVertex.pos_of_cons_Ci[j] = -1;
			curVertex.linked[j] = 0;
		}
		
	}

	//初始化COM集合，从文件中读取
	public void initCOM(int scaningfactor){
		
		try{
			
			ArrayList<Integer> tmpCOM = new ArrayList();
			
			BufferedReader in = new BufferedReader(
							new FileReader(
							new File("COM_"+scaningfactor+".txt")));
			
			String line = in.readLine();
			String[] tmp;
			
			while(line != null){
				
				tmp = line.split(" ");
				for(int i=0; i<tmp.length; i++){
					
					tmpCOM.add(Integer.parseInt(tmp[i]));
				}
				
				line = in.readLine();
			}
			
			in.close();
			
			COM = new Integer[tmpCOM.size()];
			tmpCOM.toArray(COM);						
			System.out.println("COM has "+tmpCOM.size());
			
			//初始化array 0/1 数组=======开始
			array = new byte[this.idVertexMap.size()];
			for(int i=0; i<array.length; i++){
				array[i] = 0;
			}
			for(int i=0; i<COM.length; i++){
				array[COM[i]] = 1;
			}
			//初始化array 0/1 数组=======结束
			
		}catch(IOException e){System.out.println(e);}	
		
	}
	
	
	public void merge(){
		
		long time1 = System.currentTimeMillis();
			
		initCOM(scaningfactor);
				
		table = new Table0(input_graph.length);	
		AllCNode = new int[10*this.idVertexMap.size()];
		
		mergeFirstAndTwo(start_of_third);
		
		mergeThird(start_of_third);
		
		System.out.println("after merge "+dup_cid);
	
		long time2 = System.currentTimeMillis();
		
		System.out.println("merge use time "+(time2-time1)+" ms");
		System.out.println("AllCNode has "+AllCNode_index);
		
	}
	
	public void mergeFirstAndTwo(int begin_of_third_part){
			
		//int count = 0;
		
		for(int i=0; i<begin_of_third_part-1; i++){
			
//			System.out.print(i+" ");
			
			DataVertex ri = input_graph[i];
			if(i==0){				
				duplication(ri);
			}			
			
			//count = count + ri.CNeighbor_length;
			
			DataVertex cons_ri = input_graph[i+1];
			duplication(cons_ri);
			
			mergeSearch(ri, cons_ri);
			
			int count_cons_ri_fix = 0;
			int count_ri_free = 0;
			
			for(int j=0; j<ri.CNeighbor_length; j++){
				
				if(ri.linked[j] == 0){
					
					int pos = ri.pos_of_cons_Ci[j];
					if(pos != -1){
						
						//进行merge工作
						cons_ri.Fixed[cons_ri.fix_index++] = cons_ri.CNeighbor[pos];						
						cons_ri.linked[pos] = 1;
						count_cons_ri_fix++;					
					}	
					else{
						
						ri.Free[ri.free_index++]=ri.CNeighbor[j];
						count_ri_free++;
					}
				}
			}
//			System.out.println(ri.CNeighbor_length+" "+count_cons_ri_fix+" "+count_ri_free);
			
			//找出ri的区间端点
			ri.Left = interval_start;
		 ri.Right = interval_start+ ri.fix_index+ ri.free_index+ cons_ri.fix_index- 1;
			
			//更新interval_end，好让Ri+1用
			interval_start = ri.Left + ri.fix_index + ri.free_index;
			
			for(int j=0; j<ri.fix_index; j++){
				
				table.addDup(ri.Fixed[j], dup_cid++);
				AllCNode[AllCNode_index++] = ri.Fixed[j];
			}
			for(int j=0; j<ri.free_index; j++){

				table.addDup(ri.Free[j], dup_cid++);
				AllCNode[AllCNode_index++] = ri.Free[j];
			}
								
			ri.CNeighbor = null;
			ri.size = null;
			ri.pos_of_cons_Ci = null;
			ri.linked = null;
			ri.Fixed = null;
			ri.Free = null;
			
		}
		
		//还有第一第二部分的最后一个点
		DataVertex last_r = input_graph[begin_of_third_part-1];
		
		int last_r_free_index = 0;
		
		for(int i=0; i<last_r.CNeighbor_length; i++){
			
			//因为在倒第二个r点的时候已经把Fixed域压满了
			if( last_r.linked[i] != 1){

				last_r.Free[last_r_free_index] = last_r.CNeighbor[i];
				last_r_free_index++;
			}
		}	
		
		//找到last_r的区间端点
		last_r.Left = interval_start;
		last_r.Right = interval_start + last_r.fix_index + last_r.free_index -1;
		
		//更新interval_end，好让Ri+1用
		interval_start = last_r.Left + last_r.fix_index + last_r.free_index;
		
		for(int j=0; j<last_r.fix_index; j++){

			table.addDup(last_r.Fixed[j], dup_cid++);					
			AllCNode[AllCNode_index++] = last_r.Fixed[j];
		}
		for(int j=0; j<last_r.free_index; j++){

			table.addDup(last_r.Free[j], dup_cid++);
			AllCNode[AllCNode_index++] = last_r.Free[j];
		}
		
		last_r.CNeighbor = null;
		last_r.size = null;
		last_r.pos_of_cons_Ci = null;
		last_r.linked = null;
		last_r.Fixed = null;
		last_r.Free = null;	
		
		//count = count + last_r.CNeighbor_length;
		
		//System.out.println(count);
	}
	
	public void mergeThird(int begin_of_third_part){
		
		//int count = 0;
		
		for(int i=begin_of_third_part; i<input_graph.length-1; i++){
			
//			System.out.print(i+" ");
			
			DataVertex ri = input_graph[i];

			if(i==begin_of_third_part){				
				duplication3(ri);
			}
			//count = count + ri.CNeighbor_length;
			
			DataVertex cons_ri = input_graph[i+1];
			
			duplication3(cons_ri);
					
			mergeSearch(ri, cons_ri);
			
			int count_cons_ri_fix = 0;
			int count_ri_free = 0;
			
			for(int j=0; j<ri.CNeighbor_length; j++){
				
				if(ri.linked[j] == 0){
					
					int pos = ri.pos_of_cons_Ci[j];
					if(pos != -1){
						
						//进行merge工作
						cons_ri.Fixed[cons_ri.fix_index++] = cons_ri.CNeighbor[pos];						
						cons_ri.linked[pos] = 1;
						count_cons_ri_fix++;					
					}	
					else{
						
						ri.Free[ri.free_index++]=ri.CNeighbor[j];
						count_ri_free++;
					}
				}
			}
			
			
//			System.out.println(ri.CNeighbor_length+" "+count_cons_ri_fix+" "+count_ri_free);
			
			//找出ri的区间端点
			ri.Left = interval_start;
			if(i == input_graph.length-2){
				//不能算cons_ri的fix部分了
				ri.Right = interval_start+ ri.fix_index+ ri.free_index-1;
			}
			else{
				ri.Right = interval_start+ ri.fix_index+ ri.free_index+ cons_ri.fix_index- 1;
			}
		 	
			//更新interval_end，好让Ri+1用
			interval_start = ri.Left + ri.fix_index + ri.free_index;
			
			for(int j=0; j<ri.fix_index; j++){

				table.addDup(ri.Fixed[j], dup_cid++);	
				AllCNode[AllCNode_index++] = ri.Fixed[j];
			}
			for(int j=0; j<ri.free_index; j++){

				table.addDup(ri.Free[j], dup_cid++);	
				AllCNode[AllCNode_index++] = ri.Free[j];
			}
			
			ri.CNeighbor = null;
			ri.size = null;
			ri.pos_of_cons_Ci = null;
			ri.linked = null;
			ri.Fixed = null;
			ri.Free = null;

		}
		
		//System.out.println(count);
	}
	
	public void mergeSearch(DataVertex ri, DataVertex cons_ri){
		
		for(int j=0; j<ri.CNeighbor_length;j++){
			
			ri.pos_of_cons_Ci[j] = findOccur(ri.CNeighbor[j], cons_ri);
		}							
	}
	
	public int findOccur(int Ci, DataVertex ri){
		
		int index = -2;
		
		while(index == -2 ){
			
			if(ri.marker < ri.CNeighbor_length){
				
				//如果marker小于数组的长度
				//int curCNode = ri.CNeighbor[ri.marker];
				
				if(ri.CNeighbor[ri.marker] == Ci){
					//index = ri.marker;
					return ri.marker;
				}
				else if(ri.CNeighbor[ri.marker] > Ci){
					//index = -1;
					return -1;
				}
				else{
					ri.marker++;
				}
		    }
			else{
				//marker一直加，加到最后越界也没找到说明没有
				//index = -1;
				return -1;
			}
		}
		
		return index;
	}
	
	public ArrayList<Integer> findXMLElementID(String label){
		
		return DataVertex.labelToVertexIDMap.get(label);
	}
	
	public boolean touchThirdCheck(String query_label){
		
		for(int i=start_of_third; i<input_graph.length; i++){
			
			if(input_graph[i].label.equals(query_label)){
				
				return true;
			}
		}
		return false;
	}
	
	public int findOne(byte[] array, int cur_pos){
		
		for(int i=cur_pos; i<array.length; i++){
			if(array[i] == 1){
				return i;
			}
		}
		return -1;
	}
	
	//这个是从cur_pos往右找0
	public int findZero(byte[] array, int cur_pos){
		
		for(int i=cur_pos; i<array.length; i++){
			if(array[i] == 0){
				return i;
			}
		}
		return -1;
	}
	
	//这个是从cur_pos往左找0
	public int findZeroLeft(byte[] array, int cur_pos){
		
		for(int i=cur_pos; i>=0; i--){
			if(array[i] == 0){
				return i;
			}
		}
		return -1;
	}
	
	//根据target来优化过的query dot generation
	public void output_query_positionCOM2(String label, String targetlabel, String fileAddress){
		
		try {
			
			BufferedWriter o = new BufferedWriter(
					new FileWriter(
					new File(fileAddress+""+label+"_queryCOM2.txt")));
			
			byte[] queryDupCidArray = new byte[AllCNode_index];
			for(int i=0; i<queryDupCidArray.length; i++){
				queryDupCidArray[i] = 0;
			}
			byte[] queryPriCidArray = new byte[this.idVertexMap.size()];
			for(int i=0; i<queryPriCidArray.length; i++){
				queryPriCidArray[i] = 0;
			}
			
			ArrayList<Integer> list = findXMLElementID(label);
			for(int i=0; i<list.size(); i++){
				
				DataVertex curVertex = this.idVertexMap.get(list.get(i));
				
				//如果不拆开SCC，curVertex就会为null
				//这里为了检测555个重复的blue dot是不是出自于SCC，就只好不拆开SCC了
				if(curVertex!=null){
					
					if(curVertex.THIRD){ //只考虑第三部分的点
						
						for(int j=curVertex.Left; j<=curVertex.Right; j++){
							
							queryDupCidArray[j] = 1;
						}
					}
				}
			}
			
			//找出所有的primitive cid
			for(int i=0; i<queryDupCidArray.length; i++){
				if(queryDupCidArray[i] == 1){
					queryPriCidArray[ AllCNode[i] ] = 1;
				}
			}
			
			//找出这些primitive cid所对应的所有duplicated Cid
			ArrayList<Integer> queryAllDupCid = new ArrayList();
			
			for(int i=0; i<queryPriCidArray.length; i++){
				
				if(queryPriCidArray[i] == 1){
					
					queryAllDupCid.addAll(table.getDupSet(i));
				}				
			}
			
			System.out.println("query all dup cid has [COM] "+queryAllDupCid.size());
			
			
			//此时我要根据b的区间来判断是否输出某个a区间，假设给定query=a//b
			//方法是对任意一个b区间s，我看看s的左端点所对应的dup cid,a的所有dup
			//是不是包含，如果包含就输出a的所有dup中从s的左端点开始的区间
			byte[] queryAllDupCidArray = new byte[AllCNode_index];
			for(int i=0; i<queryAllDupCidArray.length; i++){
				queryAllDupCidArray[i] = 0;
			}
			for(int i=0; i<queryAllDupCid.size(); i++){
				queryAllDupCidArray[ queryAllDupCid.get(i) ] = 1;
			}
			queryAllDupCid.clear(); //清空以节约内存
			
			//现在读入b的targetNormal.txt
			BufferedReader in = new BufferedReader(
					new FileReader(
					new File(fileAddress+targetlabel+"_targetCOM.txt")));
			
			String line = in.readLine();
			String[] tmp;
			while(line != null){
				
				tmp = line.split(" ");
				
				//仅仅需要最左端
				int left_end_of_target = Integer.parseInt(tmp[0]);
				
				if(queryAllDupCidArray[left_end_of_target] == 1){
					
					/*//则left_end就是一个query区间的左端点，现在就要找出右端点
					int interval_end = findZero(queryAllDupCidArray, left_end);
					o.write(""+left_end+" "+(interval_end-1)+"\n");
					
					//然后清0,queryAllDupCidArray的left_end到interval_end-1之间部分
					for(int i=left_end; i<interval_end; i++){
						
						queryAllDupCidArray[i] = 0;
					}*/
					
					//从left_end_of_target往左找到第一个0，往右找到第一个0
					int interval_start = findZeroLeft(queryAllDupCidArray, left_end_of_target);
					int interval_end = findZero(queryAllDupCidArray, left_end_of_target);
					
					if(interval_start == -1){
						interval_start = -1;
					}
					if(interval_end == -1){
						interval_end = queryAllDupCidArray.length;
					}
					
					o.write(""+(interval_start+1)+" "+(interval_end-1)+"\n");
					//然后清0,queryAllDupCidArray的interval_start+1
					//到interval_end-1之间部分
					for(int i=interval_start+1; i<interval_end; i++){
						
						queryAllDupCidArray[i] = 0;
					}
					
				}
				
				line = in.readLine();
			}
			
			queryAllDupCidArray = null;
			
			o.close();
			in.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	//没有优化的query dot generation
	public void output_query_positionCOM(String label, String fileAddress){

		try {
			
			BufferedWriter o = new BufferedWriter(
					new FileWriter(
					new File(fileAddress+""+label+"_queryCOM.txt")));
			
			byte[] queryDupCidArray = new byte[AllCNode_index];
			for(int i=0; i<queryDupCidArray.length; i++){
				queryDupCidArray[i] = 0;
			}
			byte[] queryPriCidArray = new byte[this.idVertexMap.size()];
			for(int i=0; i<queryPriCidArray.length; i++){
				queryPriCidArray[i] = 0;
			}
			
			ArrayList<Integer> list = findXMLElementID(label);
			for(int i=0; i<list.size(); i++){
				
				DataVertex curVertex = this.idVertexMap.get(list.get(i));
				
				//如果不拆开SCC，curVertex就会为null
				//这里为了检测555个重复的blue dot是不是出自于SCC，就只好不拆开SCC了
				if(curVertex!=null){
					
					if(curVertex.THIRD){ //只考虑第三部分的点
						
						for(int j=curVertex.Left; j<=curVertex.Right; j++){
							
							queryDupCidArray[j] = 1;
						}
					}
				}
			}
			
			//找出所有的primitive cid
			for(int i=0; i<queryDupCidArray.length; i++){
				if(queryDupCidArray[i] == 1){
					queryPriCidArray[ AllCNode[i] ] = 1;
				}
			}
			
			//找出这些primitive cid所对应的所有duplicated Cid
			ArrayList<Integer> queryAllDupCid = new ArrayList();
			
			for(int i=0; i<queryPriCidArray.length; i++){
				
				if(queryPriCidArray[i] == 1){
					
					queryAllDupCid.addAll(table.getDupSet(i));
				}				
			}
			
			System.out.println("query all dup cid has [COM] "+queryAllDupCid.size());
			
			
			//第三个版本的数组中找区间算法（不用排序了，遍历两遍即可）
			byte[] array_for_interval = new byte[AllCNode_index];
			//先赋上值
			for(int i = 0; i<queryAllDupCid.size(); i++){				
				array_for_interval[ queryAllDupCid.get(i) ] = 1;
			}
			
			long time1 = System.nanoTime();
			//再找出线段来=============
			int start_address = 0;
			int end_address = 0;
			int interval_num = 0; //interval的数目
		
			start_address = findOne(array_for_interval, 0);
			if(start_address == -1){
				o.close();
				return;
			}
			o.write(start_address+" ");
			
			while(true){
				
				end_address = findZero(array_for_interval, start_address);
				if(end_address == -1){
					o.write(""+(array_for_interval.length-1)+"\n");
					interval_num++;
					break;
				}
				o.write(""+(end_address-1)+"\n");
				interval_num++;
				start_address = findOne(array_for_interval, end_address);
				if(start_address == -1){
					break;
				}
				o.write(start_address+" ");
			}
			//线段已经找出==============
			long time2 = System.nanoTime();
			
			//System.out.println("Query Dot Gen v1 time: "+(time2-time1)+" nano sec");			
			//System.out.println("interval_num is "+interval_num);
			
			o.close();
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//读入InterHist生成的"inter_in_COM.txt", 生成inter_out_COM.txt
	public void output_inter_positionCOM(){
		
		try{
			
			BufferedReader in = new BufferedReader(
					new FileReader(
					new File("inter_in_COM.txt")));
					
			BufferedWriter o = new BufferedWriter(
					new FileWriter(
					new File("inter_out_COM.txt")));

			byte[] queryDupCidArray = new byte[AllCNode_index];
			for(int i=0; i<queryDupCidArray.length; i++){
				queryDupCidArray[i] = 0;
			}
			byte[] queryPriCidArray = new byte[this.idVertexMap.size()];
			for(int i=0; i<queryPriCidArray.length; i++){
				queryPriCidArray[i] = 0;
			}
			
			
			String line = in.readLine();
			String tmp[];

			//把query interval所包含的dup cid找出来
			while(line != null){
				
				tmp = line.split(" ");
				
				for(int i=Integer.parseInt(tmp[0]); i<=Integer.parseInt(tmp[1]); i++){
					
					queryDupCidArray[i] = 1;
				}
				
				line = in.readLine();
			}
			
			//找出所有的primitive cid
			for(int i=0; i<queryDupCidArray.length; i++){
				if(queryDupCidArray[i] == 1){
					queryPriCidArray[ AllCNode[i] ] = 1;
				}
			}
			
			//找出这些primitive cid所对应的所有duplicated Cid
			ArrayList<Integer> queryAllDupCid = new ArrayList();
			
			for(int i=0; i<queryPriCidArray.length; i++){
				
				if(queryPriCidArray[i] == 1){
					
					queryAllDupCid.addAll(table.getDupSet(i));
				}				
			}
			
			System.out.println("inter query all dup cid has [COM] "+queryAllDupCid.size());
			
	
			//第三个版本的数组中找区间算法（不用排序了，遍历两遍即可）
			byte[] array_for_interval = new byte[AllCNode_index];
			//先赋上值
			for(int i = 0; i<queryAllDupCid.size(); i++){				
				array_for_interval[ queryAllDupCid.get(i) ] = 1;
			}
			
			long time1 = System.nanoTime();
			//再找出线段来=============
			int start_address = 0;
			int end_address = 0;
			int interval_num = 0; //interval的数目
		
			start_address = findOne(array_for_interval, 0);
			if(start_address == -1){
				o.close();
				return;
			}
			o.write(start_address+" ");
			
			while(true){
				
				end_address = findZero(array_for_interval, start_address);
				if(end_address == -1){
					o.write(""+(array_for_interval.length-1)+"\n");
					interval_num++;
					break;
				}
				o.write(""+(end_address-1)+"\n");
				interval_num++;
				start_address = findOne(array_for_interval, end_address);
				if(start_address == -1){
					break;
				}
				o.write(start_address+" ");
			}
			//线段已经找出==============
			long time2 = System.nanoTime();
			
			//System.out.println("Query Dot Gen v1 time: "+(time2-time1)+" nano sec");			
			//System.out.println("interval_num is "+interval_num);
			
			o.close();
			in.close();
		}
		catch(IOException e){
			System.out.println(e);
		}
	}
	
	
	//读入InterHist生成的"inter_in_Normal.txt", 生成inter_out_Normal.txt
	public void output_inter_positionNormal(){
		
		try{
			BufferedReader in = new BufferedReader(
					new FileReader(
					new File("inter_in_Normal.txt")));
			
			
			BufferedWriter o = new BufferedWriter(
					new FileWriter(
					new File("inter_out_Normal.txt")));

			byte[] queryDupCidArray = new byte[AllCNode_index];
			for(int i=0; i<queryDupCidArray.length; i++){
				queryDupCidArray[i] = 0;
			}
			byte[] queryPriCidArray = new byte[this.idVertexMap.size()];
			for(int i=0; i<queryPriCidArray.length; i++){
				queryPriCidArray[i] = 0;
			}
			
			
			String line = in.readLine();
			String tmp[];

			//把query interval所包含的dup cid找出来
			while(line != null){
				
				tmp = line.split(" ");
				
				for(int i=Integer.parseInt(tmp[0]); i<=Integer.parseInt(tmp[1]); i++){
					
					queryDupCidArray[i] = 1;
				}
				
				line = in.readLine();
			}
			
			//找出所有的primitive cid
			for(int i=0; i<queryDupCidArray.length; i++){
				if(queryDupCidArray[i] == 1){
					queryPriCidArray[ AllCNode[i] ] = 1;
				}
			}
			
			//找出这些primitive cid所对应的所有duplicated Cid
			ArrayList<Integer> queryAllDupCid = new ArrayList();
			
			for(int i=0; i<queryPriCidArray.length; i++){
				
				if(queryPriCidArray[i] == 1){
					
					queryAllDupCid.addAll(table.getDupSet(i));
				}				
			}
			
			System.out.println("inter query all dup cid has [Normal] "+queryAllDupCid.size());
			
	
			//第三个版本的数组中找区间算法（不用排序了，遍历两遍即可）
			byte[] array_for_interval = new byte[AllCNode_index];
			//先赋上值
			for(int i = 0; i<queryAllDupCid.size(); i++){				
				array_for_interval[ queryAllDupCid.get(i) ] = 1;
			}
			
			long time1 = System.nanoTime();
			//再找出线段来=============
			int start_address = 0;
			int end_address = 0;
			int interval_num = 0; //interval的数目
		
			start_address = findOne(array_for_interval, 0);
			if(start_address == -1){
				o.close();
				return;
			}
			o.write(start_address+" ");
			
			while(true){
				
				end_address = findZero(array_for_interval, start_address);
				if(end_address == -1){
					o.write(""+(array_for_interval.length-1)+"\n");
					interval_num++;
					break;
				}
				o.write(""+(end_address-1)+"\n");
				interval_num++;
				start_address = findOne(array_for_interval, end_address);
				if(start_address == -1){
					break;
				}
				o.write(start_address+" ");
			}
			//线段已经找出==============
			long time2 = System.nanoTime();
			
			//System.out.println("Query Dot Gen v1 time: "+(time2-time1)+" nano sec");			
			//System.out.println("interval_num is "+interval_num);
			
			o.close();
			in.close();
	
		}
		catch(IOException e){
			System.out.println(e);
		}
	}
	
	
	
	//根据target来优化过的query dot generation
	public void output_query_positionNormal2(String label, String targetlabel, String fileAddress){
		
		
		try{
			
			boolean touch_third = false;	
			
			byte[] queryDupCidArray = new byte[AllCNode_index];
			for(int i=0; i<queryDupCidArray.length; i++){
				queryDupCidArray[i] = 0;
			}
			byte[] queryPriCidArray = new byte[this.idVertexMap.size()];
			for(int i=0; i<queryPriCidArray.length; i++){
				queryPriCidArray[i] = 0;
			}
			
			File infile=new File(fileAddress+""+label+"_queryNormal2.txt");
			FileWriter fileW = new FileWriter(infile);
			BufferedWriter o = new BufferedWriter(fileW);
			
			ArrayList<Integer> list = findXMLElementID(label);
			System.out.println("query xml elements has "+list.size());
			
			for(int i=0; i<list.size(); i++){
				
				DataVertex curVertex = this.idVertexMap.get(list.get(i));
				
				//如果不拆开SCC，curVertex就会为null
				//这里为了检测555个重复的blue dot是不是出自于SCC，就只好不拆开SCC了
				if(curVertex!=null){
					
					if(curVertex.THIRD){
						touch_third = true;
					}
					
					for(int j=curVertex.Left; j<=curVertex.Right; j++){
						
						queryDupCidArray[j] = 1;
					}
				}
			}
			
			
			//找出所有的primitive cid
			for(int i=0; i<queryDupCidArray.length; i++){
				if(queryDupCidArray[i] == 1){
					queryPriCidArray[ AllCNode[i] ] = 1;
				}
			}
			
			//如果touch_third,就要把COM也要加到queryPrimitiveCid集合中
			if(touch_third){
				
				for(int i=0; i<COM.length; i++){
					queryPriCidArray[ COM[i] ] = 1;
				}
			}
			
			//找出这些primitive cid所对应的所有duplicated Cid
			ArrayList<Integer> queryAllDupCid = new ArrayList();
			
			for(int i=0; i<queryPriCidArray.length; i++){
				
				if(queryPriCidArray[i] == 1){
					
					queryAllDupCid.addAll(table.getDupSet(i));
				}				
			}
			
			queryPriCidArray = null;
			queryDupCidArray = null;
			
			System.out.println("query all dup cid has [Normal] "+queryAllDupCid.size());
			
			//此时我要根据b的区间来判断是否输出某个a区间，假设给定query=a//b
			//方法是对任意一个b区间s，我看看s的左端点所对应的dup cid,a的所有dup
			//是不是包含，如果包含就输出a的所有dup中从s的左端点开始的区间
			byte[] queryAllDupCidArray = new byte[AllCNode_index];
			for(int i=0; i<queryAllDupCidArray.length; i++){
				queryAllDupCidArray[i] = 0;
			}
			for(int i=0; i<queryAllDupCid.size(); i++){
				queryAllDupCidArray[ queryAllDupCid.get(i) ] = 1;
			}
			queryAllDupCid.clear(); //清空以节约内存
			
			//现在读入b的targetNormal.txt
			BufferedReader in = new BufferedReader(
					new FileReader(
					new File(fileAddress+targetlabel+"_targetNormal.txt")));
			
			String line = in.readLine();
			String[] tmp;
			while(line != null){
				
				tmp = line.split(" ");
				
				//仅仅需要最左端
				int left_end_of_target = Integer.parseInt(tmp[0]);
				
				if(queryAllDupCidArray[left_end_of_target] == 1){
					
					/*//则left_end就是一个query区间的左端点，现在就要找出右端点
					int interval_end = findZero(queryAllDupCidArray, left_end);
					o.write(""+left_end+" "+(interval_end-1)+"\n");
					
					//然后清0,queryAllDupCidArray的left_end到interval_end-1之间部分
					for(int i=left_end; i<interval_end; i++){
						
						queryAllDupCidArray[i] = 0;
					}*/
					
					//从left_end_of_target往左找到第一个0，往右找到第一个0
					int interval_start = findZeroLeft(queryAllDupCidArray, left_end_of_target);
					int interval_end = findZero(queryAllDupCidArray, left_end_of_target);
					
					if(interval_start == -1){
						interval_start = -1;
					}
					if(interval_end == -1){
						interval_end = queryAllDupCidArray.length;
					}
					
					o.write(""+(interval_start+1)+" "+(interval_end-1)+"\n");
					//然后清0,queryAllDupCidArray的interval_start+1
					//到interval_end-1之间部分
					for(int i=interval_start+1; i<interval_end; i++){
						
						queryAllDupCidArray[i] = 0;
					}
					
				}
				
				line = in.readLine();
			}
			
			queryAllDupCidArray = null;
			
			in.close();
			o.close();
		}
		catch(IOException e){
			System.out.println(e);
		}	
		
	}
	
	//没有优化的query dot generation
	public void output_query_positionNormal(String label, String fileAddress){
		
		try{
			
			boolean touch_third = false;	
			
			byte[] queryDupCidArray = new byte[AllCNode_index];
			for(int i=0; i<queryDupCidArray.length; i++){
				queryDupCidArray[i] = 0;
			}
			byte[] queryPriCidArray = new byte[this.idVertexMap.size()];
			for(int i=0; i<queryPriCidArray.length; i++){
				queryPriCidArray[i] = 0;
			}
			
			File infile=new File(fileAddress+""+label+"_queryNormal.txt");
			FileWriter fileW = new FileWriter(infile);
			BufferedWriter o = new BufferedWriter(fileW);
			
			ArrayList<Integer> list = findXMLElementID(label);
			System.out.println("query xml elements has "+list.size());
			
			for(int i=0; i<list.size(); i++){
				
				DataVertex curVertex = this.idVertexMap.get(list.get(i));
				
				//如果不拆开SCC，curVertex就会为null
				//这里为了检测555个重复的blue dot是不是出自于SCC，就只好不拆开SCC了
				if(curVertex!=null){
					
					if(curVertex.THIRD){
						touch_third = true;
					}
					
					for(int j=curVertex.Left; j<=curVertex.Right; j++){
						
						queryDupCidArray[j] = 1;
					}
				}
			}
			
			
			//找出所有的primitive cid
			for(int i=0; i<queryDupCidArray.length; i++){
				if(queryDupCidArray[i] == 1){
					queryPriCidArray[ AllCNode[i] ] = 1;
				}
			}
			
			//如果touch_third,就要把COM也要加到queryPrimitiveCid集合中
			if(touch_third){
				
				for(int i=0; i<COM.length; i++){
					queryPriCidArray[ COM[i] ] = 1;
				}
			}
			
			
			//找出这些primitive cid所对应的所有duplicated Cid
			ArrayList<Integer> queryAllDupCid = new ArrayList();
			
			for(int i=0; i<queryPriCidArray.length; i++){
				
				if(queryPriCidArray[i] == 1){
					
					queryAllDupCid.addAll(table.getDupSet(i));
				}				
			}
			
			System.out.println("query all dup cid has [Normal] "+queryAllDupCid.size());
			
			
			//第三个版本的数组中找区间算法（不用排序了，遍历两遍即可）
			byte[] array_for_interval = new byte[AllCNode_index];
			//先赋上值
			for(int i = 0; i<queryAllDupCid.size(); i++){				
				array_for_interval[ queryAllDupCid.get(i) ] = 1;
			}
			
			long time1 = System.nanoTime();
			//再找出线段来=============
			int start_address = 0;
			int end_address = 0;
			int interval_num = 0; //interval的数目
		
			start_address = findOne(array_for_interval, 0);
			if(start_address == -1){
				o.close();
				return;
			}
			o.write(start_address+" ");
			
			while(true){
				
				end_address = findZero(array_for_interval, start_address);
				if(end_address == -1){
					o.write(""+(array_for_interval.length-1)+"\n");
					interval_num++;
					break;
				}
				o.write(""+(end_address-1)+"\n");
				interval_num++;
				start_address = findOne(array_for_interval, end_address);
				if(start_address == -1){
					break;
				}
				o.write(start_address+" ");
			}
			//线段已经找出==============
			long time2 = System.nanoTime();
			
			//System.out.println("Query Dot Gen v1 time: "+(time2-time1)+" nano sec");			
			//System.out.println("interval_num is "+interval_num);
			
			o.close();
		}
		catch(IOException e){
			System.out.println(e);
		}	

	}
	
	public void output_query_position2(String label, String targetlabel, String fileAddress){
		
		output_query_positionNormal2(label, targetlabel, fileAddress);
		output_query_positionCOM2(label, targetlabel, fileAddress);
	}
	
	public void output_query_position(String label, String fileAddress){
		
		output_query_positionNormal(label,fileAddress);
		output_query_positionCOM(label,fileAddress);
	}
	
	public void output_target_position(String label, String fileAddress){
		
		try{
			
			BufferedWriter out_normal = new BufferedWriter(
							new FileWriter(
							new File(fileAddress+""+label+"_targetNormal.txt")));
			
			BufferedWriter out_COM = new BufferedWriter(
					new FileWriter(
					new File(fileAddress+""+label+"_targetCOM.txt")));
			
			ArrayList<Integer> list = findXMLElementID(label);
			System.out.println("target xml elements has "+list.size());
			
			for(int i=0; i<list.size(); i++){
				
				DataVertex curVertex = this.idVertexMap.get(list.get(i));
				
				//如果不拆开SCC，curVertex就会为null
				//这里为了检测555个重复的blue dot是不是出自于SCC，就只好不拆开SCC了
				if(curVertex!=null){
					
					if(curVertex.THIRD){
						
						out_COM.write(""+curVertex.Left+" "+curVertex.Right+"\n");
					}
					else{
						
						out_normal.write(""+curVertex.Left+" "+curVertex.Right+"\n");
					}
				}	
			}
			
			out_COM.close();
			out_normal.close();
		}
		catch(IOException e){
			System.out.println(e);
		}
	}
	
	
	public static void main(String[] args){
		
		int start_of_third = 15444;
		int scaningfactor = 1;
		
		//File xmlFile = new File("auction.xml");
		File xmlFile=new File("I:\\Test Source Data\\xmarkgeneration\\xmark1\\xmark0.xml");			
		//File xmlFile = new File("../data/xmark"+scaningfactor+"/xmark0.xml");
		
		
		MergingPro9 g = new MergingPro9(xmlFile, scaningfactor, start_of_third);

		g.merge();
		
		//String fileAddress = "D:/MyProject/SelectivityEst4/lab/";
		String fileAddress = "";
		//g.output_query_position("open_auction", fileAddress);
		//g.output_target_position("emph", fileAddress);
		//System.out.println(g.findXMLElementID("closed_auctions"));
		
		g.output_target_position("incategory", fileAddress);
		//g.output_query_positionNormal2("open_auction", "incategory", fileAddress);
		//g.output_query_positionCOM2("open_auctions", "bidder", fileAddress);
		
	}
}
