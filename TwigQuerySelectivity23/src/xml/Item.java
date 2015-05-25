class Item{
	private String firstText = null;
 
	public void setFirstText(String str){
		firstText =  str;
	}
 
	public String getFirstText(){		
		if(firstText == null){
			return null;
		}else{
			return firstText;
		}
	}
}
