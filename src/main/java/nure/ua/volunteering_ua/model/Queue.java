package nure.ua.volunteering_ua.model;

import nure.ua.volunteering_ua.model.user.SocialCategory;
import nure.ua.volunteering_ua.model.user.VolunteeringType;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Comparator;
import java.util.List;


public class Queue {


    public Pair<Integer, List<Aid_Request>> getQueueNumber(SocialCategory socialCategory, VolunteeringType volunteeringType, List<Aid_Request> requests) {


//        requests.sort(Comparator.comparingInt(Aid_Request::getQueueNumber));
//
//        int lastQueueNumber = requests.size();
//        for(int i = requests.size(); i >= 0;){
//            if (getPriority(requests.get(i).getCustomer().getUser().getSocialCategory().toString()) < getPriority(socialCategory.toString())
//                    || (getPriority(requests.get(i).getCustomer().getUser().getSocialCategory().toString()) == getPriority(socialCategory.toString())
//                    && getPriorityByVolunteeringType(requests.get(i).getVolunteeringType().toString()) < getPriorityByVolunteeringType(volunteeringType.toString()))){
//                lastQueueNumber --;
//            }
//            else {
//                break;
//            }
//        }

        // Sort the requests based on social category and volunteering type priorities
        requests.sort(Comparator.comparingInt((Aid_Request request) -> getPriority(request.getCustomer().getUser().getSocialCategory().toString()))
                .thenComparingInt(request -> getPriorityByVolunteeringType(request.getVolunteeringType().toString())));

        // Find the index where to insert the new request based on social category and volunteering type
//        int insertIndex = 0;
//        for (Aid_Request request : requests) {
//            if (getPriority(request.getCustomer().getUser().getSocialCategory().toString()) < getPriority(socialCategory.toString())
//                    || (getPriority(request.getCustomer().getUser().getSocialCategory().toString()) == getPriority(socialCategory.toString())
//                    && getPriorityByVolunteeringType(request.getVolunteeringType().toString()) < getPriorityByVolunteeringType(volunteeringType.toString()))) {
//                insertIndex++;
//            } else {
//                break;
//            }
//        }

        //TODO iterating from last element
        // if lastElement getSocialCategory == ElementToAddSocialCategory && lastElement getVolunteeringType == ElementToAdd getVolunteeringType
        // return last element queue number+1
        // else if lastElement getSocialCategory <= ElementToAddSocialCategory && lastElement getVolunteeringType == ElementToAdd getVolunteeringType
        int lastQueueNumber = requests.size();
        int newQueueNumberIndex = lastQueueNumber;
        for (int i = requests.size()-1; i >= 0; ) {
            if (getPriority(requests.get(i).getCustomer().getUser().getSocialCategory().toString()) < getPriority(socialCategory.toString())
                    || (getPriority(requests.get(i).getCustomer().getUser().getSocialCategory().toString()) == getPriority(socialCategory.toString())
                    && getPriorityByVolunteeringType(requests.get(i).getVolunteeringType().toString()) < getPriorityByVolunteeringType(volunteeringType.toString())
                    || getPriorityByVolunteeringType(requests.get(i).getVolunteeringType().toString()) == getPriorityByVolunteeringType(volunteeringType.toString()))) {
                newQueueNumberIndex = i;
                break;
            } else {
                i--;
            }
        }


        int newQueueNumber;
        if (newQueueNumberIndex < requests.size()) {
            newQueueNumber = requests.get(newQueueNumberIndex).getQueueNumber();
            updateQueueNumbers(requests, newQueueNumberIndex, newQueueNumber + 1);
        } else {
            newQueueNumber = requests.size() + 1;
        }
        return Pair.of(newQueueNumber, requests);
    }

    private void updateQueueNumbers(List<Aid_Request> requests, int startIndex, int initialQueueNumber) {
        for (int i = startIndex; i < requests.size(); i++) {
            Aid_Request request = requests.get(i);
            request.setQueueNumber(initialQueueNumber++);
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
