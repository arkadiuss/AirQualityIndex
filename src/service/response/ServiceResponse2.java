package service.response;

@FunctionalInterface
public interface ServiceResponse2<First, Second>{
    void onResponse(First o, Second t);
}

