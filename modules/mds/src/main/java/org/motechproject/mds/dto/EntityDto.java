package org.motechproject.mds.dto;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.hibernate.validator.HibernateValidationProviderResolver;
import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.HibernateValidatorConfiguration;
import org.motechproject.mds.exception.MDSValidationErrors;
import org.motechproject.mds.exception.MDSValidationException;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

/**
 * The <code>EntityDto</code> class contains only basic information about an entity like id, name,
 * module and namespace.
 */
public class EntityDto implements Serializable {

    private static final long serialVersionUID = -687991492884005033L;

    private static final int MAX_ENTITY_NAME_LENGTH = 30;

    @JsonProperty
    private String id;

    @NotNull(message = "key:mds.validation.error.entityNameNotExists")
    @Size(max = MAX_ENTITY_NAME_LENGTH, message = "key:mds.validation.error.entityNameLength")
    @Pattern(regexp = "^[a-zA-Z]+[a-zA-Z0-9_]*", message = "key:mds.validation.error.entityNameNonAlphanumeric")
    @JsonProperty
    private String name;

    @JsonProperty
    private String module;

    @JsonProperty
    private String namespace;

    @JsonIgnore
    private Validator validator;

    public EntityDto() {
        this(null, null);
    }

    public EntityDto(String id, String name) {
        this(id, name, null);
    }

    public EntityDto(String id, String name, String module) {
        this(id, name, module, null);
    }

    public EntityDto(String id, String name, String module, String namespace) {
        this.id = id;
        this.name = name;
        this.module = module;
        this.namespace = namespace;
        HibernateValidatorConfiguration validatorConfiguration = Validation.byProvider(HibernateValidator.class)
                .providerResolver(new HibernateValidationProviderResolver()).configure();
        ValidatorFactory validatorFactory = validatorConfiguration.buildValidatorFactory();
        this.validator = validatorFactory.getValidator();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getModule() {
        return module;
    }

    public String getNamespace() {
        return namespace;
    }

    public void validate() {
        Set<ConstraintViolation<EntityDto>> violations = validator.validate(this);

        if (violations.isEmpty()) {
            return;
        }

        MDSValidationErrors errors = new MDSValidationErrors();
        for (ConstraintViolation<EntityDto> violation : violations) {
            errors.add(violation.getMessage());
        }
        throw new MDSValidationException(errors.allErrorKeys());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, name, module, namespace);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final EntityDto other = (EntityDto) obj;

        return Objects.equals(this.id, other.id)
                && Objects.equals(this.name, other.name)
                && Objects.equals(this.module, other.module)
                && Objects.equals(this.namespace, other.namespace);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format(
                "EntityDto{id='%s', name='%s', module='%s', namespace='%s'}",
                id, name, module, namespace
        );
    }

}
