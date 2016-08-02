package com.marcoabreu.att.profile;

import com.marcoabreu.att.profile.data.AttProfile;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

/**
 * Created by AbreuM on 29.07.2016.
 */
public class ProfileMarshaller {
    public static AttProfile loadProfile(String path) {
        return null;
    }

    public static void saveProfile(AttProfile profile, String path) throws JAXBException {
        //File file = new File("C:\\file.xml");
        JAXBContext jaxbContext = JAXBContext.newInstance(AttProfile.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

        // output pretty printed
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        //jaxbMarshaller.marshal(customer, file);
        jaxbMarshaller.marshal(profile, System.out);
    }
}
