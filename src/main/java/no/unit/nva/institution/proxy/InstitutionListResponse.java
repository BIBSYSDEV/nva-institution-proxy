package no.unit.nva.institution.proxy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

public class InstitutionListResponse implements List<InstitutionResponse> {
    private final List<InstitutionResponse> institutionResponseList;

    protected InstitutionListResponse() {
        institutionResponseList = new ArrayList<>();
    }

    public InstitutionListResponse(List<InstitutionResponse> institutionResponseList) {
        this.institutionResponseList = institutionResponseList;
    }

    @Override
    public int size() {
        return institutionResponseList.size();
    }

    @Override
    public boolean isEmpty() {
        return institutionResponseList.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return institutionResponseList.contains(o);
    }

    @Override
    public Iterator<InstitutionResponse> iterator() {
        return institutionResponseList.iterator();
    }

    @Override
    public Object[] toArray() {
        return institutionResponseList.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return institutionResponseList.toArray(a);
    }

    @Override
    public boolean add(InstitutionResponse institutionResponse) {
        return institutionResponseList.add(institutionResponse);
    }

    @Override
    public void add(int i, InstitutionResponse institutionResponse) {
        institutionResponseList.add(i, institutionResponse);
    }

    @Override
    public boolean remove(Object o) {
        return institutionResponseList.remove(o);
    }

    @Override
    public InstitutionResponse remove(int i) {
        return institutionResponseList.remove(i);
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return institutionResponseList.containsAll(collection);
    }

    @Override
    public boolean addAll(Collection<? extends InstitutionResponse> collection) {
        return institutionResponseList.addAll(collection);
    }

    @Override
    public boolean addAll(int i, Collection<? extends InstitutionResponse> collection) {
        return addAll(i, collection);
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        return institutionResponseList.removeAll(collection);
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        return retainAll(collection);
    }

    @Override
    public void clear() {
        institutionResponseList.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InstitutionListResponse)) {
            return false;
        }
        InstitutionListResponse that = (InstitutionListResponse) o;
        return Objects.equals(institutionResponseList, that.institutionResponseList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(institutionResponseList);
    }

    @Override
    public InstitutionResponse get(int i) {
        return institutionResponseList.get(i);
    }

    @Override
    public InstitutionResponse set(int i, InstitutionResponse institutionResponse) {
        return institutionResponseList.set(i, institutionResponse);
    }

    @Override
    public int indexOf(Object o) {
        return institutionResponseList.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return institutionResponseList.lastIndexOf(o);
    }

    @Override
    public ListIterator<InstitutionResponse> listIterator() {
        return institutionResponseList.listIterator();
    }

    @Override
    public ListIterator<InstitutionResponse> listIterator(int i) {
        return institutionResponseList.listIterator(i);
    }

    @Override
    public List<InstitutionResponse> subList(int start, int end) {
        return institutionResponseList.subList(start, end);
    }
}
