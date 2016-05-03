package csu.org.dependency.volley;

public class Listener {

    public interface Response<T>{
        void onResponse(T obj);
    }

    public interface Error{
        void onError(String msg);
    }
}
