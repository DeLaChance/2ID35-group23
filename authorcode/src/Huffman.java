import java.util.ArrayList;
class Code{
	
	int digit = -1;
	int occur_finish_position = -1;
	
	public Code(int digit_input, int occur_finish_position_input){
		digit = digit_input;
		occur_finish_position = occur_finish_position_input;
	}
	
	public String toString(){
		return ""+digit+" "+occur_finish_position;
	}
}

public class Huffman {
	
	
	public static void zip_primitiveCid2(DataVertex vertex){
		
		int[] tmp = new int[vertex.primitiveCid.length];
		
		tmp[0] = vertex.primitiveCid[0];
		
		for(int i=1; i<vertex.primitiveCid.length; i++){
			
			tmp[i] = vertex.primitiveCid[i] - vertex.primitiveCid[i-1];
		}

		int index = 0;
		
		int curInt;
		int pos;
		for(int i=0; i<tmp.length;){
			
			curInt = tmp[i];
			pos = findDiff(curInt, i, tmp);
			if(pos == -1){
				pos = tmp.length;
			}

			vertex.primitiveCid_zip2_digit[index] = tmp[i];
			vertex.primitiveCid_zip2_pos[index] = pos-1;
			index++;
			
			i = pos;
		}
		
		vertex.primitiveCid = null;
			
	}
	
	
	//这个函数是预先不知道primitiveCid_zip的长度
	public static void zip_primitiveCid(DataVertex vertex){
		
		ArrayList<Code> list = new ArrayList();
		
		int[] tmp = new int[vertex.primitiveCid.length];
		
		tmp[0] = vertex.primitiveCid[0];
		
		for(int i=1; i<vertex.primitiveCid.length; i++){
			
			tmp[i] = vertex.primitiveCid[i] - vertex.primitiveCid[i-1];
		}
		
		int curInt;
		int pos;
		for(int i=0; i<tmp.length;){
			
			curInt = tmp[i];
			pos = findDiff(curInt, i, tmp);
			if(pos == -1){
				pos = tmp.length;
			}
			Code code = new Code(tmp[i], (pos-1));
			list.add(code);
			i = pos;
		}
		
		vertex.primitiveCid_zip = list;
		vertex.primitiveCid = null;
	}
	
	
	public static void unzip_CNeighbor(DataVertex vertex){
		
		int[] tmp = new int[vertex.CNeighbor_length];
		int start = 0;
		for(int i=0; i<vertex.CNeighbor_zip.length; i++){
			Code code = vertex.CNeighbor_zip[i];
			for(int j=start; j<=code.occur_finish_position; j++){
				tmp[j] = code.digit;
			}
			start = code.occur_finish_position+1;
		}
		
		vertex.CNeighbor = new int[vertex.CNeighbor_length];
		vertex.CNeighbor[0] = tmp[0];
		for(int i=1; i<vertex.CNeighbor_length; i++){
			vertex.CNeighbor[i] = tmp[i] + vertex.CNeighbor[i-1];
		}
		
		tmp = null;
	}
	
	
	
	public static void zip_CNeighbor(DataVertex vertex){
		
		ArrayList<Code> list = new ArrayList();
		
		int[] tmp = new int[vertex.CNeighbor.length];
		
		tmp[0] = vertex.CNeighbor[0];
		
		for(int i=1; i<vertex.CNeighbor.length; i++){
			
			tmp[i] = vertex.CNeighbor[i] - vertex.CNeighbor[i-1];
		}
		
		int curInt;
		int pos;
		for(int i=0; i<tmp.length;){
			
			curInt = tmp[i];
			pos = findDiff(curInt, i, tmp);
			if(pos == -1){
				pos = tmp.length;
			}
			Code code = new Code(tmp[i], (pos-1));
			list.add(code);
			i = pos;
		}
		
		//vertex.CNeighbor_zip = list;
		
		vertex.CNeighbor_zip = new Code[list.size()];
		list.toArray(vertex.CNeighbor_zip);
		
		vertex.CNeighbor = null;
	}
	
	//从index开始往后找，看看第一个不等于v的数在哪里
	public static int findDiff(int v, int index, int[] list){
		
		for(int i=index; i<list.length; i++){
			if(list[i] != v){
				return i;
			}
		}
		
		return -1;
	}
	
	public void test(){
		
		int[] tmp = {15, 1, 1, 1, 6, 6, 6};
		int curInt;
		
		for(int i=0; i<tmp.length;){
			
			curInt = tmp[i];
			//int occur = findDiff(curInt, i, tmp) -1 - i;
			int pos = findDiff(curInt, i, tmp);
			if(pos == -1){
				pos = tmp.length;
			}
			System.out.println(tmp[i]+" "+(pos-1));
			i = pos;
		}
	}
	
	public static void main(String[] args){
		
		DataVertex v = new DataVertex("SCC");
		int[] tmp = {15, 16, 17, 18, 24, 30, 36};
		v.CNeighbor = tmp;
		v.CNeighbor_length = 7;
		
		//Huffman hf = new Huffman();
		//hf.zip(v);
		Huffman.zip_CNeighbor(v);
		System.out.println(v.CNeighbor_zip);
		Huffman.unzip_CNeighbor(v);
		for(int i=0; i<v.CNeighbor_length; i++){
			System.out.println(v.CNeighbor[i]);
		}
		
	}
}
