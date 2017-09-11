package com.indcgroup.loadresult;

import com.indcgroup.model.MyResponse;

/**
 * Created by thefa on 05/08/2017.
 */

public interface ApiResponse {
    void onFinishCommunication(MyResponse result);
}
