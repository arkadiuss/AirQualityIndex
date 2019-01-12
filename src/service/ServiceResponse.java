package service;

@FunctionalInterface
public interface ServiceResponse<One, Two>{
    void onResponse(One o, Two t);
}
