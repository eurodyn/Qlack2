package com.eurodyn.qlack2.webdesktop.impl.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.Query;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.collections.CollectionUtils;

/**
 * The persistent class for the application database table.
 *
 */
@Entity
@Table(name = "wd_application")
public class Application implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "app_uuid")
	private String appUuid;
	@Column(name = "title_key")
	private String titleKey;
	@Column(name = "description_key")
	private String descriptionKey;
	private String version;
	private String path;
	@Column(name = "app_index")
	private String index;
	@Column(name = "multiple_instances")
	private boolean multipleInstances;
	@Column(name = "restrict_access")
	private boolean restrictAccess;
	@Column(name = "translations_group")
	private String translationsGroup;
	private String icon;
	@Column(name = "icon_small")
	private String iconSmall;
	private int width;
	@Column(name = "min_width")
	private int minWidth;
	private int height;
	@Column(name = "min_height")
	private int minHeight;
	private boolean resizable;
	private boolean system;
	private boolean minimizable;
	private boolean maximizable;
	private boolean closable;
	private boolean draggable;
	@Column(name = "bundle_symbolic_name")
	private String bundleSymbolicName;
	private boolean active;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "added_on")
	private Date addedOn;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "last_deployed_on")
	private Date lastDeployedOn;
	@Column(name = "bg_color")
	private String bgColor;
	@Column(name = "show_title")
	private boolean showTitle;

	public Application() {
	}

	public static Application getApplicationForSymbolicName(String symbolicName, EntityManager em) {
		Query query = em.createQuery("SELECT a FROM Application a WHERE a.bundleSymbolicName = :symbolicName");
		query.setParameter("symbolicName", symbolicName);
		@SuppressWarnings("unchecked")
		List<Application> queryResult = query.getResultList();
		if (CollectionUtils.isEmpty(queryResult)) {
			return null;
		} else {
			return queryResult.get(0);
		}
	}

	public static List<Application> getAllApps(Boolean active, EntityManager em) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Application> cq = cb.createQuery(Application.class);
		Root<Application> root = cq.from(Application.class);
		cq = cq.select(root);
		cq = cq.where(cb.isNotNull(root.<String> get("bundleSymbolicName")));
		if (active != null) {
			Predicate pr = cb.equal(root.get("active"), active);
			cq = cq.where(cb.and(cq.getRestriction(), pr));
		}
		TypedQuery<Application> query = em.createQuery(cq);
		return query.getResultList();
	}

	public String getAppUuid() {
		return appUuid;
	}

	public void setAppUuid(String appUuid) {
		this.appUuid = appUuid;
	}

	public String getTitleKey() {
		return titleKey;
	}

	public void setTitleKey(String titleKey) {
		this.titleKey = titleKey;
	}

	public String getDescriptionKey() {
		return descriptionKey;
	}

	public void setDescriptionKey(String descriptionKey) {
		this.descriptionKey = descriptionKey;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public boolean isMultipleInstances() {
		return multipleInstances;
	}

	public void setMultipleInstances(boolean multipleInstances) {
		this.multipleInstances = multipleInstances;
	}

	public boolean isRestrictAccess() {
		return restrictAccess;
	}

	public void setRestrictAccess(boolean restrictAccess) {
		this.restrictAccess = restrictAccess;
	}

	public String getTranslationsGroup() {
		return translationsGroup;
	}

	public void setTranslationsGroup(String translationsGroup) {
		this.translationsGroup = translationsGroup;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getIconSmall() {
		return iconSmall;
	}

	public void setIconSmall(String iconSmall) {
		this.iconSmall = iconSmall;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getMinWidth() {
		return minWidth;
	}

	public void setMinWidth(int minWidth) {
		this.minWidth = minWidth;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getMinHeight() {
		return minHeight;
	}

	public void setMinHeight(int minHeight) {
		this.minHeight = minHeight;
	}

	public boolean isResizable() {
		return resizable;
	}

	public void setResizable(boolean resizable) {
		this.resizable = resizable;
	}

	public boolean isMinimizable() {
		return minimizable;
	}

	public void setMinimizable(boolean minimizable) {
		this.minimizable = minimizable;
	}

	public boolean isMaximizable() {
		return maximizable;
	}

	public void setMaximizable(boolean maximizable) {
		this.maximizable = maximizable;
	}

	public boolean isClosable() {
		return closable;
	}

	public void setClosable(boolean closable) {
		this.closable = closable;
	}

	public boolean isDraggable() {
		return draggable;
	}

	public void setDraggable(boolean draggable) {
		this.draggable = draggable;
	}

	public String getBundleSymbolicName() {
		return bundleSymbolicName;
	}

	public void setBundleSymbolicName(String bundleSymbolicName) {
		this.bundleSymbolicName = bundleSymbolicName;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Date getAddedOn() {
		return addedOn;
	}

	public void setAddedOn(Date addedOn) {
		this.addedOn = addedOn;
	}

	public Date getLastDeployedOn() {
		return lastDeployedOn;
	}

	public void setLastDeployedOn(Date lastDeployedOn) {
		this.lastDeployedOn = lastDeployedOn;
	}

	public String getBgColor() {
		return bgColor;
	}

	public void setBgColor(String bgColor) {
		this.bgColor = bgColor;
	}

	public boolean isSystem() {
		return system;
	}

	public void setSystem(boolean system) {
		this.system = system;
	}

	public boolean isShowTitle() {
		return showTitle;
	}

	public void setShowTitle(boolean showTitle) {
		this.showTitle = showTitle;
	}

}