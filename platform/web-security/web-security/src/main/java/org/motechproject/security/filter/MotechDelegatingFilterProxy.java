package org.motechproject.security.filter;

import org.motechproject.security.model.PermissionDto;
import org.motechproject.security.service.MotechPermissionService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;

public class MotechDelegatingFilterProxy extends DelegatingFilterProxy {
    private static final String ADMIN_MODE_FILE = "admin-mode.conf";

    private Filter anonymousFilter;
    private boolean isAdminMode = false;

    public MotechDelegatingFilterProxy(String targetBeanName, WebApplicationContext wac) {
        super(targetBeanName, wac);

        isAdminMode = checkAdminMode();
        anonymousFilter = new MotechAnonymousAuthenticationFilter(wac.getBean(MotechPermissionService.class));
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (isAdminMode) {
            anonymousFilter.doFilter(request, response, filterChain);
        } else {
            super.doFilter(request, response, filterChain);
        }
    }

    private boolean checkAdminMode() {
        Properties p = new Properties();
        boolean adminModeProperty = false;

        File adminModeFile = new File(String.format("%s/.motech/%s", System.getProperty("user.home"), ADMIN_MODE_FILE));

        if (adminModeFile.exists()) {
            try (InputStream in = new FileInputStream(adminModeFile)) {
                p.load(new InputStreamReader(in));
                adminModeProperty = Boolean.valueOf(p.getProperty("admin.mode"));

                adminModeFile.delete();
            } catch (IOException e) {
                logger.debug("Cannot admin mode read file", e);
            }
        }

        return adminModeProperty;
    }

    private class MotechAnonymousAuthenticationFilter extends AnonymousAuthenticationFilter {
        private MotechPermissionService permissionService;

        public MotechAnonymousAuthenticationFilter(final MotechPermissionService permissionService) {
            super("adminMode");
            this.permissionService = permissionService;
        }

        @Override
        public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
            List<GrantedAuthority> permissions = createPermissionList();

            if(permissions.size() > getAuthorities().size()) {
                this.getAuthorities().clear();
                this.getAuthorities().addAll(permissions);

                SecurityContextHolder.getContext().setAuthentication(null);
            }

            super.doFilter(req, res, chain);
        }

        private List<GrantedAuthority> createPermissionList() {
            List<String> list = new ArrayList<>();

            for (PermissionDto dto : permissionService.getPermissions()) {
                list.add(dto.getPermissionName());
            }

            return createAuthorityList(list.toArray(new String[list.size()]));
        }
    }
}
