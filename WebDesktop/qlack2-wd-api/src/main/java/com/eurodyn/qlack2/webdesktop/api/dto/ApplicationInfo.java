package com.eurodyn.qlack2.webdesktop.api.dto;

import java.io.Serializable;

/**
 * Provides information regarding how an application should be treated by the
 * Web Desktop. This class is created by the webdesktop-impl when discovering a
 * newly deployed application by reading the respective configuration properties
 * of the deployed application from OSGI-INF/wd-app.yaml.
 * 
 * @author European Dynamics SA
 *
 */
public class ApplicationInfo implements Serializable {
	private static final long serialVersionUID = -5309196132803867967L;

	private Identification identification;

	private Instantiation instantiation;

	// Information regarding how this application is rendered in menus, icons,
	// etc.
	private Menu menu;
	
	// Information regarding the behavior of the window created for this 
	// application.
	private Window window;
	
	// Applications can remain installed on Web Desktop however marked as
	// inactive.
	private boolean active = true;

	/**
	 * Information uniquely identifying this application.
	 */
	public static class Identification {
		// The unique ID of the application can be anything as long as it is
		// universally unique and contains characters to be used in HTML. A 
		// safe choice is, obviously, a UUID.
		private String uniqueId;
		
		// The title of the application. The title will be automatically looked
		// up in translations.
		private String titleKey;
		
		// The description of the application. The description will be 
		// automatically looked up in translations. 
		private String descriptionKey;
		
		// The version of the application.
		private String version;

		public String getUniqueId() {
			return uniqueId;
		}

		public void setUniqueId(String uniqueId) {
			this.uniqueId = uniqueId;
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
	}

	/**
	 * Information pertaining to how this application should be instantiated
	 * in the Web Desktop.
	 */
	public static class Instantiation {
		// The URL under which this application lives. Applications deployed
		// within the Web Desktop environment should start with a relative URL.
		// Appliactions deployed in any other environment should start with
		// http:// or https://.
		private String path;
		
		// The path to the index/starting page of the application. If the path
		// ends with a ".js", Web Desktop will only get the Javascript file
		// and execute it on its context without opening an application window;
		// this is useful to deploy scripts that can appear as apps in the
		// start menu.
		private String index;
		
		// Whether multiple instances of this application are allowed to co-exist.
		private boolean multipleInstances;
		
		// Whether this is a application available to all or not.
		private boolean restrictAccess;
		
		// The name of the translations group for this application.
		private String translationsGroup;

		public boolean getRestrictAccess() {
			return restrictAccess;
		}

		public void setRestrictAccess(boolean restrictAccess) {
			this.restrictAccess = restrictAccess;
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

		public boolean getMultipleInstances() {
			return multipleInstances;
		}

		public void setMultipleInstances(boolean multipleInstances) {
			this.multipleInstances = multipleInstances;
		}

		public String getTranslationsGroup() {
			return translationsGroup;
		}

		public void setTranslationsGroup(String translationsGroup) {
			this.translationsGroup = translationsGroup;
		}
	}

	/**
	 * Defines how the application appears in menus.
	 */
	public static class Menu {
		// The icon to be displayed on the start menu. There are different types
		// of icons you may use, e.g.
		// {icon-x}cog - Uses a cog icon from  http://metroui.org.ua/font.html.		
		// {image-x}images/icon.png - Uses the image provided.
		// {gallery-x}image1,image2,... - Shows a gallery icon fetching images from
		// the list of provided URLs.
		//
		// "-x" denotes the size of the icon. Available values are:
		// small, square, wide, large
		private String icon;
		
		// The icon to be displayed on the title-bar of your application window.
		// The different type of icons are:
		// {icon}cog - Uses a cog icon from  http://metroui.org.ua/font.html.
		// {image}images/icon.png - Uses the image provided.
		private String iconSmall;

		// The background color CSS class of the tile of your application.
		// See: http://metroui.org.ua/colors.html
		private String bgColor;
		
		// Indicates this is a system app. System apps are rendered on the
		// right-side of the start menu.
		private boolean system;
		
		// Indicates whether the title should be shown or not.
		private boolean showTitle;
		
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

	/**
	 * Defines the behaviors of the appliaction window. 
	 */
	public static class Window {
		// The starting width of the application window.
		private int width;
		
		// The minimum width the application window can shrink to.
		private int minWidth;
		
		// The starting height of the application window.
		private int height;
		
		// The minimum height of the application window.
		private int minHeight;
		
		// Can the application window be resized?
		private boolean resizable;
		
		// Can the application window be maximised?
		private boolean maximizable;
		
		// Can the application window be minimised?
		private boolean minimizable;
		
		// Can the application window be closed?
		private boolean closable;
		
		// Can the application window be dragged around?
		private boolean draggable;

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

		public boolean isMaximizable() {
			return maximizable;
		}

		public void setMaximizable(boolean maximizable) {
			this.maximizable = maximizable;
		}

		public boolean isMinimizable() {
			return minimizable;
		}

		public void setMinimizable(boolean minimizable) {
			this.minimizable = minimizable;
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

	}

	public Identification getIdentification() {
		return identification;
	}

	public void setIdentification(Identification identification) {
		this.identification = identification;
	}

	public Instantiation getInstantiation() {
		return instantiation;
	}

	public void setInstantiation(Instantiation instantiation) {
		this.instantiation = instantiation;
	}

	public Menu getMenu() {
		return menu;
	}

	public void setMenu(Menu menu) {
		this.menu = menu;
	}

	public Window getWindow() {
		return window;
	}

	public void setWindow(Window window) {
		this.window = window;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
}
