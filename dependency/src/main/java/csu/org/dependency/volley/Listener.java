package csu.org.dependency.volley;

/**
 * Created by shinado on 15/8/27.
 */
public class Listener {

    public interface Response<T>{
        void onResponse(T obj);
    }

    public interface Error{
        void onError(String msg);
    }
}
