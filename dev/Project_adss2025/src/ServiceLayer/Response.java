package ServiceLayer;

public class Response<T> {

    private String errorMsg;
    private T returnValue;

    /**
     * Creates a response with both an error message and return value.
     * @param errorMsg
     * @param returnValue
     */
    public Response(String errorMsg, T returnValue){
        this.errorMsg=errorMsg;
        this.returnValue=returnValue;
    }

    /**
     * A constructor for response, with no return value input.
     * @param errorMsg
     */
    public Response(String errorMsg){
        this.errorMsg=errorMsg;
    }

    public Boolean isError(){
        return errorMsg!=null;
    }


    public String getErrorMsg() {
        return errorMsg;
    }



    public T getReturnValue() {
        return returnValue;
    }


}