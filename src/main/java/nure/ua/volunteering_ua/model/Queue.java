package nure.ua.volunteering_ua.model;

import nure.ua.volunteering_ua.exeption.CustomException;
import nure.ua.volunteering_ua.model.user.SocialCategory;
import nure.ua.volunteering_ua.model.user.VolunteeringType;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.HttpStatus;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;


public class Queue {


    public Pair<Integer, List<Aid_Request>> getQueueNumber(
            SocialCategory socialCategory,
            VolunteeringType volunteeringType,
            List<Aid_Request> requests
    ) {
        int newQueueNumber = evaluateQueueNumber(socialCategory, volunteeringType, requests);
        updateQueueNumber(requests, newQueueNumber);
        return Pair.of(newQueueNumber, requests);
    }

    private int evaluateQueueNumber(SocialCategory socialCategory, VolunteeringType volunteeringType, List<Aid_Request> requests) {
        requests.sort(Comparator.comparingInt(Aid_Request::getQueueNumber));
        int position = requests.size();
        if (position == 0) {
            return 1;
        }else if(position == 1){
            Aid_Request request = requests.get(0);
            if (getPriority(request.getCustomer().getUser().getSocialCategory().toString()) > getPriority(socialCategory.toString())) {
               return 2;
            } else if (getPriority(request.getCustomer().getUser().getSocialCategory().toString()) <= getPriority(socialCategory.toString())
                    && getPriorityByVolunteeringType(request.getVolunteeringType().toString()) > getPriorityByVolunteeringType(volunteeringType.toString())) {
                return 2;
            } else if (getPriority(request.getCustomer().getUser().getSocialCategory().toString()) <= getPriority(socialCategory.toString())
                    && getPriorityByVolunteeringType(request.getVolunteeringType().toString()) <= getPriorityByVolunteeringType(volunteeringType.toString())) {
                return 1;
            }
        }
        for (int i = position - 1; i >= 0; ) {
            Aid_Request request = requests.get(i);
            if (getPriority(request.getCustomer().getUser().getSocialCategory().toString()) > getPriority(socialCategory.toString())) {
                i--;
            } else if (getPriority(request.getCustomer().getUser().getSocialCategory().toString()) <= getPriority(socialCategory.toString())
                    && getPriorityByVolunteeringType(request.getVolunteeringType().toString()) > getPriorityByVolunteeringType(volunteeringType.toString())) {
                i--;
            } else if (getPriority(request.getCustomer().getUser().getSocialCategory().toString()) <= getPriority(socialCategory.toString())
                    && getPriorityByVolunteeringType(request.getVolunteeringType().toString()) <= getPriorityByVolunteeringType(volunteeringType.toString())) {
                position = i;
                break;
            }
        }
        return requests.get(position).getQueueNumber() + 1;
    }

    private void updateQueueNumber(List<Aid_Request> requests, int startIndex) {
        for (int i = startIndex - 1; i < requests.size(); i++) {
            int newIndex = requests.get(i).getQueueNumber();
            requests.get(i).setQueueNumber(newIndex + 1);
        }
    }

    public List<Aid_Request> updateQueueNumberAfterComplete(List<Aid_Request> requests, int queueNumber) {
        requests.sort(Comparator.comparingInt(Aid_Request::getQueueNumber));
        Aid_Request aid_request = requests.stream().filter(request -> request.getQueueNumber() == queueNumber)
                .findAny()
                .orElseThrow(() ->
                        new CustomException(
                                "There is no request with provided queue number",
                                HttpStatus.BAD_REQUEST)
                );
        aid_request.setQueueNumber(-1);
        if (requests.size() == 1) {
            return requests;
        } else {
            requests.stream()
                    .filter(request -> request.getQueueNumber() > queueNumber)
                    .forEach(request -> request.setQueueNumber(request.getQueueNumber() - 1));

            return requests;
        }
    }

    public int getPriority(String socialStatus) {
        int priority;

        switch (socialStatus) {
            case "ORPHANS_AND_CHILDREN_WITHOUT_PARENTAL_CARE":
                priority = 1;
                break;
            case "REFUGEES_AND_DISPLACED_PERSONS":
                priority = 2;
                break;
            case "VICTIMS_OF_DOMESTIC_VIOLENCE":
                priority = 3;
                break;
            case "PEOPLE_WITH_DISABILITIES":
                priority = 4;
                break;
            case "PENSIONER":
                priority = 5;
                break;
            case "SINGLE_PARENT_FAMILIES":
                priority = 6;
                break;
            case "LARGE_FAMILIES":
                priority = 7;
                break;
            case "LOW_INCOME_FAMILIES":
                priority = 8;
                break;
            case "UNEMPLOYED_PERSONS":
                priority = 9;
                break;
            case "HOMELESS_PERSONS":
                priority = 10;
                break;
            case "NO_CATEGORY":
                priority = 11;
                break;
            default:
                priority = 0; // Default priority if the social status is not recognized
                break;
        }

        return priority;
    }

    public int getPriorityByVolunteeringType(String volunteeringType) {
        int priority;

        switch (volunteeringType) {
            case "SOCIAL":
                priority = 1;
                break;
            case "ANIMALS":
                priority = 2;
                break;
            case "ENVIRONMENTAL":
                priority = 3;
                break;
            default:
                priority = 0; // Default priority if the social status is not recognized
                break;
        }

        return priority;
    }

}
