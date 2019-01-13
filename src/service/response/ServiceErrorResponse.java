package service.response;

@FunctionalInterface
public interface ServiceErrorResponse{
    void onError(Throwable t);
}
