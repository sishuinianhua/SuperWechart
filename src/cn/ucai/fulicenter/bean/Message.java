package cn.ucai.fulicenter.bean;

import java.io.Serializable;

public class Message implements Serializable{

	private boolean success;

		private String msg;

	public Message(){

	}
	public Message(String msg, boolean success) {
		this.msg = msg;
		this.success = success;
	}

		public void setSuccess(boolean success){
			this.success = success;
		}
		public boolean getSuccess(){
			return this.success;
		}
		public void setMsg(String msg){
			this.msg = msg;
		}
		public String getMsg(){
			return this.msg;
		}

	@Override
	public String toString() {
		return "Message{" +
				"msg='" + msg + '\'' +
				", success=" + success +
				'}';
	}
}
	

