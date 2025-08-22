package org.example.dto;

import org.example.models.data.ClientData;
import org.example.models.form.ClientForm;
import org.example.pojo.ClientPojo;
import org.example.util.StringUtil;
import org.springframework.http.HttpStatus;

public class DtoHelperClient {
    public static void normalize(ClientPojo client) throws ApiException {
        client.setName(StringUtil.toLowerCasee(client.getName()));
    }

    public static void checkNameIsValid(ClientPojo client)  throws ApiException {
        if(StringUtil.isEmpty(client.getName())) {
            throw new ApiException("name cannot be empty", HttpStatus.BAD_REQUEST);
        }
    }

public static void checkExist(ClientPojo client) throws ApiException {
        if(client == null){
            throw new ApiException("client does not exist", HttpStatus.BAD_REQUEST);
        }
}

public static void checkAlreadyExist(ClientPojo client) throws ApiException {
        if(client !=null){
            throw new ApiException("client already exist", HttpStatus.BAD_REQUEST);
        }
}

   public static ClientPojo convert(ClientForm form) throws ApiException {
        ClientPojo p = new ClientPojo();
        p.setName(form.getName());
        return p;
    }

   public static ClientData convert(ClientPojo pojo) throws ApiException {
       ClientData d = new ClientData();
       d.setId(pojo.getId());
d.setName(pojo.getName());

       return d;
   }

   public static ClientPojo updateClient(ClientForm form,ClientPojo pojo) throws ApiException {
        pojo.setName(form.getName());
        return pojo;
   }


}
