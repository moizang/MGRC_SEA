package fr.sigl.epita.imoe.minigrc.bo;

import fr.sigl.epita.imoe.minigrc.beans.ClientEntity;
import fr.sigl.epita.imoe.minigrc.beans.EvenementEntity;
import fr.sigl.epita.imoe.minigrc.beans.UserloginEntity;
import fr.sigl.epita.imoe.minigrc.dao.DAOFactory;

import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import java.sql.Date;
import java.util.*;

/**
 * Created by Chaf on 12/29/2015.
 */
public class EvenementBO {

    public void createEvent(String clientId, String type, String createur, String date, String description) {
        EvenementEntity evenementEntity = new EvenementEntity();
        evenementEntity.setEventClientId(Integer.parseInt(clientId));
        evenementEntity.setEventType(type);
        evenementEntity.setEventDate(Date.valueOf(date));
        evenementEntity.setEventCreateur(createur);
        evenementEntity.setEventLastupdate(Date.valueOf(date));
        evenementEntity.setEventLastupdater(createur);
        evenementEntity.setEventDescription(description);

        DAOFactory.getInstance().getEvenementDAO().persist(evenementEntity);
    }
    
    public void updateEvent(EvenementEntity event) {

        DAOFactory.getInstance().getEvenementDAO().update(event);
    }

    public List searchEventsWithRegion(String type_searchform, String date_searchform, String region) {
        ClientEntity clientEntity = new ClientEntity();
        clientEntity.setClientRegion(region);
        List clientList = DAOFactory.getInstance().getClientDAO().findByExample(clientEntity);

        List evenementList = new ArrayList<>();

        for (int i = 0; i < clientList.size(); ++i) {
            ClientEntity clientInList = (ClientEntity) clientList.get(i);
            EvenementEntity evenementEntity = new EvenementEntity();
            if (!Objects.equals(type_searchform, ""))
                evenementEntity.setEventType(type_searchform);
            if (null != date_searchform && !Objects.equals(date_searchform, ""))
                evenementEntity.setEventDate(Date.valueOf(date_searchform));
            evenementEntity.setEventClientId(clientInList.getClientId());
            evenementList.addAll(DAOFactory.getInstance().getEvenementDAO().findByExample(evenementEntity));
        }

        Collections.sort(evenementList, new Comparator<EvenementEntity>() {
            @Override
            public int compare(EvenementEntity e1, EvenementEntity e2) {
                return e1.getEventDate().compareTo(e2.getEventDate());
            }
        });

        return evenementList;
    }

    public EvenementEntity getEvenement(String selectedEventId) {
        EvenementEntity evenementEntity = DAOFactory.getInstance().getEvenementDAO().findById(Integer.parseInt(selectedEventId));

        return evenementEntity;
    }

    public List getLastTenEvents(String userName) {
        UserloginEntity userloginEntity = new UserloginEntity();
        userloginEntity.setUserLogin(userName);
        List userList = DAOFactory.getInstance().getUserloginDAO().findByExample(userloginEntity);

        userloginEntity = (UserloginEntity) userList.get(0);

        List eventList = searchEventsWithRegion("", "", userloginEntity.getUserRegion());

        Collections.sort(eventList, new Comparator<EvenementEntity>() {
            @Override
            public int compare(EvenementEntity e1, EvenementEntity e2) {
                return e2.getEventLastupdate().compareTo(e1.getEventLastupdate());
            }
        });
        if (eventList.size() > 10)
            return eventList.subList(0, 9);
        else
            return eventList;
    }
}
