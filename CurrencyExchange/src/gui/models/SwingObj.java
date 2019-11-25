package gui.models;

public enum SwingObj {
	BUTTON(0, 24), COMBO(1, 22), LABEL(3, 18), TEXT(2, 20);
	
    private final int _value;
    private final int _height;

    SwingObj(int value, int height) {
    	_value = value;
    	_height = height;
    }

    public int getV() {
        return _value;
    }
    
    public int getH() {
        return _height;
    }
    
}
