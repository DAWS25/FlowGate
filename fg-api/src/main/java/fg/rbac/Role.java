package fg.rbac;

import java.time.LocalDateTime;

public interface Role{
    String getRoleName();
    String getProvider();
    default LocalDateTime expireAt(){
        return LocalDateTime.now().plusDays(1);
    }
}
