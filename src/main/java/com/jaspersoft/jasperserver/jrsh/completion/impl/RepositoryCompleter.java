/*
 * Copyright (C) 2005 - 2015 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased  a commercial license agreement from Jaspersoft,
 * the following license terms  apply:
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License  as
 * published by the Free Software Foundation, either version 3 of  the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero  General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public  License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.jaspersoft.jasperserver.jrsh.completion.impl;

import com.jaspersoft.jasperserver.dto.resources.ClientResourceLookup;
import com.jaspersoft.jasperserver.jaxrs.client.core.*;
import com.jaspersoft.jasperserver.jaxrs.client.core.exceptions.AuthenticationFailedException;
import com.jaspersoft.jasperserver.jaxrs.client.core.exceptions.ResourceNotFoundException;
import com.jaspersoft.jasperserver.jrsh.common.SessionHolder;
import jline.console.completer.Completer;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.jaspersoft.jasperserver.jaxrs.client.apiadapters.resources.ResourceSearchParameter.FOLDER_URI;
import static com.jaspersoft.jasperserver.jaxrs.client.apiadapters.resources.ResourceSearchParameter.RECURSIVE;

/**
 * @author Alexander Krasnyanskiy
 */
public class RepositoryCompleter implements Completer {

    public static int uniqueId = 0;
    public static List<CharSequence> bufCandidates = new ArrayList<CharSequence>();

    @Override
    public int complete(String buffer, int cursor, List<CharSequence> candidates) {

        // fixme: need refactoring (!)

        //
        // prevent completion when user move cursor back
        // and press Tab key
        //
        if (buffer != null && cursor < buffer.length()) {
            candidates.add("");
            return buffer.length();
        }

        if (uniqueId != 0) {
            if (uniqueId == hashCode()) {
                if (buffer == null || buffer.isEmpty()) {
                    candidates.add("/");
                    return 0;
                }
                List<Pair<String, Boolean>> resources;
                List<String> filteredResources = Collections.emptyList();
                try {
                    resources = Downloader.download(buffer);
                    if (!resources.isEmpty() && !buffer.equals("/")) {
                        candidates.add("/");
                        return buffer.length() + 1;
                    }
                    filteredResources = reformatResources(resources);
                    candidates.addAll(filteredResources);
                    bufCandidates.clear();
                    bufCandidates.addAll(filteredResources);
                } catch (ResourceNotFoundException e1) {
                    String root = getPreviousPath(buffer);
                    try {
                        resources = Downloader.download(root);
                        List<Pair<String, Boolean>> temp = new ArrayList<Pair<String, Boolean>>();

                        for (Pair<String, Boolean> pair : resources) {
                            String resource = pair.getKey();
                            Boolean isFolder = pair.getRight();
                            if (StringUtils.startsWith(resource, buffer)) {
                                ImmutablePair<String, Boolean> newPair =
                                        new ImmutablePair<String, Boolean>(resource, isFolder);
                                temp.add(newPair);
                            }
                        }
                        filteredResources = reformatResources(temp);
                        candidates.addAll(filteredResources);
                        bufCandidates.clear();
                        bufCandidates.addAll(filteredResources);
                    } catch (ResourceNotFoundException e2) {
                    }
                } catch (AuthenticationFailedException e3) {
                    reopenSession();
                    complete(buffer, cursor, candidates);
                }
                if (candidates.size() == 1) {
                    return buffer.lastIndexOf("/") + 1;
                }
                if (candidates.size() > 1) {
                    String lastInput = getLastInput(buffer);
                    if (compareCandidatesWithLastInput(lastInput, candidates)) {
                        return buffer.length() - lastInput.length();
                    }
                }
                return buffer.length();
            } else {
                if (buffer == null) {
                    return 0;
                } else {
                    candidates.addAll(bufCandidates);
                    if (candidates.size() > 1) {
                        String lastInput = getLastInput(buffer);
                        if (compareCandidatesWithLastInput(lastInput, candidates)) {
                            return buffer.length() - lastInput.length();
                        }
                    }
                    String lastInput = getLastInput(buffer);
                    if (compareCandidatesWithLastInput(lastInput, candidates)) {
                        return buffer.length() - lastInput.length();
                    }
                    return buffer.length();
                }
            }
        }
        // TODO: refactoring is needed
        else {
            uniqueId = hashCode();
            if (buffer == null || buffer.isEmpty()) {
                candidates.add("/");
                return 0;
            }
            List<Pair<String, Boolean>> resources;
            List<String> filteredResources = Collections.emptyList();
            try {
                resources = Downloader.download(buffer);
                if (!resources.isEmpty() && !buffer.equals("/")) {
                    candidates.add("/");
                    return buffer.length() + 1;
                }

                filteredResources = reformatResources(resources);
                candidates.addAll(filteredResources);
                bufCandidates.clear();
                bufCandidates.addAll(filteredResources);
            } catch (ResourceNotFoundException e1) {
                String root = getPreviousPath(buffer);
                try {
                    resources = Downloader.download(root);
                    List<Pair<String, Boolean>> temp = new ArrayList<Pair<String, Boolean>>();

                    for (Pair<String, Boolean> pair : resources) {
                        String resource = pair.getKey();
                        Boolean isFolder = pair.getRight();

                        if (StringUtils.startsWith(resource, buffer)) {
                            ImmutablePair<String, Boolean> newPair =
                                    new ImmutablePair<String, Boolean>(resource, isFolder);
                            temp.add(newPair);
                        }
                    }

                    filteredResources = reformatResources(temp);
                    candidates.addAll(filteredResources);
                    bufCandidates.clear();
                    bufCandidates.addAll(filteredResources);
                } catch (ResourceNotFoundException e2) {
                    // NOP
                }
            } catch (AuthenticationFailedException e3) {
                reopenSession();
                complete(buffer, cursor, candidates);
            }
            if (candidates.size() == 1) {
                return buffer.lastIndexOf("/") + 1;
            }
            if (candidates.size() > 1) {
                String lastInput = getLastInput(buffer);
                if (compareCandidatesWithLastInput(lastInput, candidates)) {
                    return buffer.length() - lastInput.length();
                }
            }
            return buffer.length();
        }
    }

    //---------------------------------------------------------------------
    // Helper methods
    //---------------------------------------------------------------------

    private void reopenSession() {
        Session session = SessionHolder.getSharedSession();
        if (session != null) {
            SessionStorage storage = session.getStorage();
            AuthenticationCredentials credentials = storage.getCredentials();
            RestClientConfiguration cfg = storage.getConfiguration();
            JasperserverRestClient client = new JasperserverRestClient(cfg);
            session = client.authenticate(credentials.getUsername(), credentials.getPassword());
            SessionHolder.save(session);
        }
    }

    String getLastInput(String buffer) {
        int idx = buffer.lastIndexOf("/");
        String s = buffer.substring(idx, buffer.length());
        if (s.equals("/")) s = "";
        if (s.startsWith("/") && s.length() > 1) s = s.substring(1, s.length());
        return s;
    }

    private boolean compareCandidatesWithLastInput(String last, List<CharSequence> candidates) {
        for (CharSequence candidate : candidates) {
            if (!candidate.toString().startsWith(last)) {
                return false;
            }
        }
        return true;
    }

    private List<String> reformatResources(List<Pair<String, Boolean>> resources) {
        List<String> list = new ArrayList<String>();
        for (Pair<String, Boolean> pair : resources) {
            String resource = pair.getLeft();
            Boolean isFolder = pair.getRight();
            String last;

            if (isFolder) {
                last = lastName(resource) + "/";
            } else {
                last = lastName(resource);
            }
            list.add(last);
        }
        return list;
    }

    public String getPreviousPath(String path) {
        int idx = StringUtils.lastIndexOf(path, "/");
        return idx > 0 ? path.substring(0, idx) : path.substring(0, idx + 1);
    }

    private String lastName(String path) {
        //return Paths.get(path).getFileName().toString(); // Java7
        return new File(path).getName();
    }

    // ---------------------------------------------------------------------
    //                            Nested Classes
    // ---------------------------------------------------------------------

    private static class Downloader {
        public static List<Pair<String, Boolean>> download(String path) {
            List<Pair<String, Boolean>> list = new ArrayList<Pair<String, Boolean>>();
            List<ClientResourceLookup> lookups;

            try {
                lookups = SessionHolder.getSharedSession()
                        .resourcesService()
                        .resources()
                        .parameter(FOLDER_URI, path)
                        .parameter(RECURSIVE, "false")
                        .search()
                        .getEntity()
                        .getResourceLookups();
            } catch (NullPointerException e) {
                return list;
            }
            for (ClientResourceLookup lookup : lookups) {
                String uri = lookup.getUri();
                String type = lookup.getResourceType();

                if ("folder".equals(type)) {
                    list.add(new ImmutablePair<String, Boolean>(uri, true));
                } else {
                    list.add(new ImmutablePair<String, Boolean>(uri, false));
                }
            }
            return list;
        }
    }
}
