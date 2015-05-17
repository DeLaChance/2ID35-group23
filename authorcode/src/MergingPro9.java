import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

/*
 * ������ļ��ж�ȡlabel��Ϣ������ÿ�ζ�����label����ʡ��label��ʱ��
 */
public class MergingPro9 extends LabelGraphFromFile{

	Integer[] COM;
	byte[] array; //���Կ��ٰѵ������ֵĵ��label�е�COMԪ���׵�
	Table0 table; //��primitive �� duplicated ��map
	int dup_cid = 0; //duplicated cid
	int interval_start = 0;
	//ArrayList<Integer> AllCNode; //��duplicated �� primitive ��map
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
	
	//����duplicate��һ�ڶ�����
	public void duplication(DataVertex curVertex){
		
		//��һ�ڶ�����ȫzip���ˣ������ȫ��unzip
		//Huffman.unzip_CNeighbor(curVertex);

		//��ʼduplication
		curVertex.pos_of_cons_Ci = new int[curVertex.CNeighbor_length];
		curVertex.linked = new byte[curVertex.CNeighbor_length];
		curVertex.Fixed = new int[curVertex.CNeighbor_length];
		curVertex.Free = new int[curVertex.CNeighbor_length];
		for(int j=0; j<curVertex.CNeighbor_length; j++){
			curVertex.pos_of_cons_Ci[j] = -1;
			curVertex.linked[j] = 0;
		}
	}
	

	//����duplicate�������֣���Ϊ����������û��ѹ�����ģ����Բ��ý�ѹ
	public void duplication3(DataVertex curVertex){
		
		curVertex.CNeighbor_length = curVertex.CNeighbor.length;
		curVertex.THIRD = true;
		
		//��ʼduplication
		curVertex.pos_of_cons_Ci = new int[curVertex.CNeighbor_length];
		curVertex.linked = new byte[curVertex.CNeighbor_length];
		curVertex.Fixed = new int[curVertex.CNeighbor_length];
		curVertex.Free = new int[curVertex.CNeighbor_length];
		for(int j=0; j<curVertex.CNeighbor_length; j++){
			curVertex.pos_of_cons_Ci[j] = -1;
			curVertex.linked[j] = 0;
		}
		
	}

	//��ʼ��COM���ϣ����ļ��ж�ȡ
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
			
			//��ʼ��array 0/1 ����=======��ʼ
			array = new byte[this.idVertexMap.size()];
			for(int i=0; i<array.length; i++){
				array[i] = 0;
			}
			for(int i=0; i<COM.length; i++){
				array[COM[i]] = 1;
			}
			//��ʼ��array 0/1 ����=======����
			
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
						
						//����merge����
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
			
			//�ҳ�ri������˵�
			ri.Left = interval_start;
		 ri.Right = interval_start+ ri.fix_index+ ri.free_index+ cons_ri.fix_index- 1;
			
			//����interval_end������Ri+1��
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
		
		//���е�һ�ڶ����ֵ����һ����
		DataVertex last_r = input_graph[begin_of_third_part-1];
		
		int last_r_free_index = 0;
		
		for(int i=0; i<last_r.CNeighbor_length; i++){
			
			//��Ϊ�ڵ��ڶ���r���ʱ���Ѿ���Fixed��ѹ����
			if( last_r.linked[i] != 1){

				last_r.Free[last_r_free_index] = last_r.CNeighbor[i];
				last_r_free_index++;
			}
		}	
		
		//�ҵ�last_r������˵�
		last_r.Left = interval_start;
		last_r.Right = interval_start + last_r.fix_index + last_r.free_index -1;
		
		//����interval_end������Ri+1��
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
						
						//����merge����
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
			
			//�ҳ�ri������˵�
			ri.Left = interval_start;
			if(i == input_graph.length-2){
				//������cons_ri��fix������
				ri.Right = interval_start+ ri.fix_index+ ri.free_index-1;
			}
			else{
				ri.Right = interval_start+ ri.fix_index+ ri.free_index+ cons_ri.fix_index- 1;
			}
		 	
			//����interval_end������Ri+1��
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
				
				//���markerС������ĳ���
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
				//markerһֱ�ӣ��ӵ����Խ��Ҳû�ҵ�˵��û��
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
	
	//����Ǵ�cur_pos������0
	public int findZero(byte[] array, int cur_pos){
		
		for(int i=cur_pos; i<array.length; i++){
			if(array[i] == 0){
				return i;
			}
		}
		return -1;
	}
	
	//����Ǵ�cur_pos������0
	public int findZeroLeft(byte[] array, int cur_pos){
		
		for(int i=cur_pos; i>=0; i--){
			if(array[i] == 0){
				return i;
			}
		}
		return -1;
	}
	
	//����target���Ż�����query dot generation
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
				
				//�������SCC��curVertex�ͻ�Ϊnull
				//����Ϊ�˼��555���ظ���blue dot�ǲ��ǳ�����SCC����ֻ�ò���SCC��
				if(curVertex!=null){
					
					if(curVertex.THIRD){ //ֻ���ǵ������ֵĵ�
						
						for(int j=curVertex.Left; j<=curVertex.Right; j++){
							
							queryDupCidArray[j] = 1;
						}
					}
				}
			}
			
			//�ҳ����е�primitive cid
			for(int i=0; i<queryDupCidArray.length; i++){
				if(queryDupCidArray[i] == 1){
					queryPriCidArray[ AllCNode[i] ] = 1;
				}
			}
			
			//�ҳ���Щprimitive cid����Ӧ������duplicated Cid
			ArrayList<Integer> queryAllDupCid = new ArrayList();
			
			for(int i=0; i<queryPriCidArray.length; i++){
				
				if(queryPriCidArray[i] == 1){
					
					queryAllDupCid.addAll(table.getDupSet(i));
				}				
			}
			
			System.out.println("query all dup cid has [COM] "+queryAllDupCid.size());
			
			
			//��ʱ��Ҫ����b���������ж��Ƿ����ĳ��a���䣬�������query=a//b
			//�����Ƕ�����һ��b����s���ҿ���s����˵�����Ӧ��dup cid,a������dup
			//�ǲ��ǰ�����������������a������dup�д�s����˵㿪ʼ������
			byte[] queryAllDupCidArray = new byte[AllCNode_index];
			for(int i=0; i<queryAllDupCidArray.length; i++){
				queryAllDupCidArray[i] = 0;
			}
			for(int i=0; i<queryAllDupCid.size(); i++){
				queryAllDupCidArray[ queryAllDupCid.get(i) ] = 1;
			}
			queryAllDupCid.clear(); //����Խ�Լ�ڴ�
			
			//���ڶ���b��targetNormal.txt
			BufferedReader in = new BufferedReader(
					new FileReader(
					new File(fileAddress+targetlabel+"_targetCOM.txt")));
			
			String line = in.readLine();
			String[] tmp;
			while(line != null){
				
				tmp = line.split(" ");
				
				//������Ҫ�����
				int left_end_of_target = Integer.parseInt(tmp[0]);
				
				if(queryAllDupCidArray[left_end_of_target] == 1){
					
					/*//��left_end����һ��query�������˵㣬���ھ�Ҫ�ҳ��Ҷ˵�
					int interval_end = findZero(queryAllDupCidArray, left_end);
					o.write(""+left_end+" "+(interval_end-1)+"\n");
					
					//Ȼ����0,queryAllDupCidArray��left_end��interval_end-1֮�䲿��
					for(int i=left_end; i<interval_end; i++){
						
						queryAllDupCidArray[i] = 0;
					}*/
					
					//��left_end_of_target�����ҵ���һ��0�������ҵ���һ��0
					int interval_start = findZeroLeft(queryAllDupCidArray, left_end_of_target);
					int interval_end = findZero(queryAllDupCidArray, left_end_of_target);
					
					if(interval_start == -1){
						interval_start = -1;
					}
					if(interval_end == -1){
						interval_end = queryAllDupCidArray.length;
					}
					
					o.write(""+(interval_start+1)+" "+(interval_end-1)+"\n");
					//Ȼ����0,queryAllDupCidArray��interval_start+1
					//��interval_end-1֮�䲿��
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
	
	//û���Ż���query dot generation
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
				
				//�������SCC��curVertex�ͻ�Ϊnull
				//����Ϊ�˼��555���ظ���blue dot�ǲ��ǳ�����SCC����ֻ�ò���SCC��
				if(curVertex!=null){
					
					if(curVertex.THIRD){ //ֻ���ǵ������ֵĵ�
						
						for(int j=curVertex.Left; j<=curVertex.Right; j++){
							
							queryDupCidArray[j] = 1;
						}
					}
				}
			}
			
			//�ҳ����е�primitive cid
			for(int i=0; i<queryDupCidArray.length; i++){
				if(queryDupCidArray[i] == 1){
					queryPriCidArray[ AllCNode[i] ] = 1;
				}
			}
			
			//�ҳ���Щprimitive cid����Ӧ������duplicated Cid
			ArrayList<Integer> queryAllDupCid = new ArrayList();
			
			for(int i=0; i<queryPriCidArray.length; i++){
				
				if(queryPriCidArray[i] == 1){
					
					queryAllDupCid.addAll(table.getDupSet(i));
				}				
			}
			
			System.out.println("query all dup cid has [COM] "+queryAllDupCid.size());
			
			
			//�������汾���������������㷨�����������ˣ��������鼴�ɣ�
			byte[] array_for_interval = new byte[AllCNode_index];
			//�ȸ���ֵ
			for(int i = 0; i<queryAllDupCid.size(); i++){				
				array_for_interval[ queryAllDupCid.get(i) ] = 1;
			}
			
			long time1 = System.nanoTime();
			//���ҳ��߶���=============
			int start_address = 0;
			int end_address = 0;
			int interval_num = 0; //interval����Ŀ
		
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
			//�߶��Ѿ��ҳ�==============
			long time2 = System.nanoTime();
			
			//System.out.println("Query Dot Gen v1 time: "+(time2-time1)+" nano sec");			
			//System.out.println("interval_num is "+interval_num);
			
			o.close();
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//����InterHist���ɵ�"inter_in_COM.txt", ����inter_out_COM.txt
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

			//��query interval��������dup cid�ҳ���
			while(line != null){
				
				tmp = line.split(" ");
				
				for(int i=Integer.parseInt(tmp[0]); i<=Integer.parseInt(tmp[1]); i++){
					
					queryDupCidArray[i] = 1;
				}
				
				line = in.readLine();
			}
			
			//�ҳ����е�primitive cid
			for(int i=0; i<queryDupCidArray.length; i++){
				if(queryDupCidArray[i] == 1){
					queryPriCidArray[ AllCNode[i] ] = 1;
				}
			}
			
			//�ҳ���Щprimitive cid����Ӧ������duplicated Cid
			ArrayList<Integer> queryAllDupCid = new ArrayList();
			
			for(int i=0; i<queryPriCidArray.length; i++){
				
				if(queryPriCidArray[i] == 1){
					
					queryAllDupCid.addAll(table.getDupSet(i));
				}				
			}
			
			System.out.println("inter query all dup cid has [COM] "+queryAllDupCid.size());
			
	
			//�������汾���������������㷨�����������ˣ��������鼴�ɣ�
			byte[] array_for_interval = new byte[AllCNode_index];
			//�ȸ���ֵ
			for(int i = 0; i<queryAllDupCid.size(); i++){				
				array_for_interval[ queryAllDupCid.get(i) ] = 1;
			}
			
			long time1 = System.nanoTime();
			//���ҳ��߶���=============
			int start_address = 0;
			int end_address = 0;
			int interval_num = 0; //interval����Ŀ
		
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
			//�߶��Ѿ��ҳ�==============
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
	
	
	//����InterHist���ɵ�"inter_in_Normal.txt", ����inter_out_Normal.txt
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

			//��query interval��������dup cid�ҳ���
			while(line != null){
				
				tmp = line.split(" ");
				
				for(int i=Integer.parseInt(tmp[0]); i<=Integer.parseInt(tmp[1]); i++){
					
					queryDupCidArray[i] = 1;
				}
				
				line = in.readLine();
			}
			
			//�ҳ����е�primitive cid
			for(int i=0; i<queryDupCidArray.length; i++){
				if(queryDupCidArray[i] == 1){
					queryPriCidArray[ AllCNode[i] ] = 1;
				}
			}
			
			//�ҳ���Щprimitive cid����Ӧ������duplicated Cid
			ArrayList<Integer> queryAllDupCid = new ArrayList();
			
			for(int i=0; i<queryPriCidArray.length; i++){
				
				if(queryPriCidArray[i] == 1){
					
					queryAllDupCid.addAll(table.getDupSet(i));
				}				
			}
			
			System.out.println("inter query all dup cid has [Normal] "+queryAllDupCid.size());
			
	
			//�������汾���������������㷨�����������ˣ��������鼴�ɣ�
			byte[] array_for_interval = new byte[AllCNode_index];
			//�ȸ���ֵ
			for(int i = 0; i<queryAllDupCid.size(); i++){				
				array_for_interval[ queryAllDupCid.get(i) ] = 1;
			}
			
			long time1 = System.nanoTime();
			//���ҳ��߶���=============
			int start_address = 0;
			int end_address = 0;
			int interval_num = 0; //interval����Ŀ
		
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
			//�߶��Ѿ��ҳ�==============
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
	
	
	
	//����target���Ż�����query dot generation
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
				
				//�������SCC��curVertex�ͻ�Ϊnull
				//����Ϊ�˼��555���ظ���blue dot�ǲ��ǳ�����SCC����ֻ�ò���SCC��
				if(curVertex!=null){
					
					if(curVertex.THIRD){
						touch_third = true;
					}
					
					for(int j=curVertex.Left; j<=curVertex.Right; j++){
						
						queryDupCidArray[j] = 1;
					}
				}
			}
			
			
			//�ҳ����е�primitive cid
			for(int i=0; i<queryDupCidArray.length; i++){
				if(queryDupCidArray[i] == 1){
					queryPriCidArray[ AllCNode[i] ] = 1;
				}
			}
			
			//���touch_third,��Ҫ��COMҲҪ�ӵ�queryPrimitiveCid������
			if(touch_third){
				
				for(int i=0; i<COM.length; i++){
					queryPriCidArray[ COM[i] ] = 1;
				}
			}
			
			//�ҳ���Щprimitive cid����Ӧ������duplicated Cid
			ArrayList<Integer> queryAllDupCid = new ArrayList();
			
			for(int i=0; i<queryPriCidArray.length; i++){
				
				if(queryPriCidArray[i] == 1){
					
					queryAllDupCid.addAll(table.getDupSet(i));
				}				
			}
			
			queryPriCidArray = null;
			queryDupCidArray = null;
			
			System.out.println("query all dup cid has [Normal] "+queryAllDupCid.size());
			
			//��ʱ��Ҫ����b���������ж��Ƿ����ĳ��a���䣬�������query=a//b
			//�����Ƕ�����һ��b����s���ҿ���s����˵�����Ӧ��dup cid,a������dup
			//�ǲ��ǰ�����������������a������dup�д�s����˵㿪ʼ������
			byte[] queryAllDupCidArray = new byte[AllCNode_index];
			for(int i=0; i<queryAllDupCidArray.length; i++){
				queryAllDupCidArray[i] = 0;
			}
			for(int i=0; i<queryAllDupCid.size(); i++){
				queryAllDupCidArray[ queryAllDupCid.get(i) ] = 1;
			}
			queryAllDupCid.clear(); //����Խ�Լ�ڴ�
			
			//���ڶ���b��targetNormal.txt
			BufferedReader in = new BufferedReader(
					new FileReader(
					new File(fileAddress+targetlabel+"_targetNormal.txt")));
			
			String line = in.readLine();
			String[] tmp;
			while(line != null){
				
				tmp = line.split(" ");
				
				//������Ҫ�����
				int left_end_of_target = Integer.parseInt(tmp[0]);
				
				if(queryAllDupCidArray[left_end_of_target] == 1){
					
					/*//��left_end����һ��query�������˵㣬���ھ�Ҫ�ҳ��Ҷ˵�
					int interval_end = findZero(queryAllDupCidArray, left_end);
					o.write(""+left_end+" "+(interval_end-1)+"\n");
					
					//Ȼ����0,queryAllDupCidArray��left_end��interval_end-1֮�䲿��
					for(int i=left_end; i<interval_end; i++){
						
						queryAllDupCidArray[i] = 0;
					}*/
					
					//��left_end_of_target�����ҵ���һ��0�������ҵ���һ��0
					int interval_start = findZeroLeft(queryAllDupCidArray, left_end_of_target);
					int interval_end = findZero(queryAllDupCidArray, left_end_of_target);
					
					if(interval_start == -1){
						interval_start = -1;
					}
					if(interval_end == -1){
						interval_end = queryAllDupCidArray.length;
					}
					
					o.write(""+(interval_start+1)+" "+(interval_end-1)+"\n");
					//Ȼ����0,queryAllDupCidArray��interval_start+1
					//��interval_end-1֮�䲿��
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
	
	//û���Ż���query dot generation
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
				
				//�������SCC��curVertex�ͻ�Ϊnull
				//����Ϊ�˼��555���ظ���blue dot�ǲ��ǳ�����SCC����ֻ�ò���SCC��
				if(curVertex!=null){
					
					if(curVertex.THIRD){
						touch_third = true;
					}
					
					for(int j=curVertex.Left; j<=curVertex.Right; j++){
						
						queryDupCidArray[j] = 1;
					}
				}
			}
			
			
			//�ҳ����е�primitive cid
			for(int i=0; i<queryDupCidArray.length; i++){
				if(queryDupCidArray[i] == 1){
					queryPriCidArray[ AllCNode[i] ] = 1;
				}
			}
			
			//���touch_third,��Ҫ��COMҲҪ�ӵ�queryPrimitiveCid������
			if(touch_third){
				
				for(int i=0; i<COM.length; i++){
					queryPriCidArray[ COM[i] ] = 1;
				}
			}
			
			
			//�ҳ���Щprimitive cid����Ӧ������duplicated Cid
			ArrayList<Integer> queryAllDupCid = new ArrayList();
			
			for(int i=0; i<queryPriCidArray.length; i++){
				
				if(queryPriCidArray[i] == 1){
					
					queryAllDupCid.addAll(table.getDupSet(i));
				}				
			}
			
			System.out.println("query all dup cid has [Normal] "+queryAllDupCid.size());
			
			
			//�������汾���������������㷨�����������ˣ��������鼴�ɣ�
			byte[] array_for_interval = new byte[AllCNode_index];
			//�ȸ���ֵ
			for(int i = 0; i<queryAllDupCid.size(); i++){				
				array_for_interval[ queryAllDupCid.get(i) ] = 1;
			}
			
			long time1 = System.nanoTime();
			//���ҳ��߶���=============
			int start_address = 0;
			int end_address = 0;
			int interval_num = 0; //interval����Ŀ
		
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
			//�߶��Ѿ��ҳ�==============
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
				
				//�������SCC��curVertex�ͻ�Ϊnull
				//����Ϊ�˼��555���ظ���blue dot�ǲ��ǳ�����SCC����ֻ�ò���SCC��
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
