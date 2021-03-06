package net.sf.uadetector.json.internal.data.deserializer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.qualitycheck.Check;
import net.sf.uadetector.internal.data.Data;
import net.sf.uadetector.internal.data.domain.Browser;
import net.sf.uadetector.internal.data.domain.BrowserPattern;
import net.sf.uadetector.internal.data.domain.BrowserType;
import net.sf.uadetector.internal.data.domain.OperatingSystem;
import net.sf.uadetector.internal.data.domain.OperatingSystemPattern;
import net.sf.uadetector.internal.data.domain.Robot;
import net.sf.uadetector.json.internal.data.JsonConverter.SerializationOption;
import net.sf.uadetector.json.internal.data.field.SerializableDataField;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public final class DataDeserializer extends AbstractDeserializer<Data> implements JsonDeserializer<Data> {

	private static <T> List<T> deserializeType(final JsonDeserializationContext context, final Map.Entry<String, JsonElement> jsonObject,
			final SerializableDataField field, final Class<T> classOfT) {
		Check.notNull(context, "context");
		Check.notNull(jsonObject, "jsonObject");
		Check.notNull(field, "field");
		Check.notNull(classOfT, "classOfT");

		final List<T> result = new ArrayList<T>();
		if (field.getName().equals(jsonObject.getKey())) {
			final JsonArray browsers = jsonObject.getValue().getAsJsonArray();
			for (final JsonElement element : browsers) {
				final T entry = context.deserialize(element, classOfT);
				if (entry != null) {
					result.add(entry);
				}
			}
		}
		return result;
	}

	public DataDeserializer(final EnumSet<SerializationOption> options) {
		super(options);
	}

	@Override
	public Data deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
		final JsonObject obj = json.getAsJsonObject();
		final Set<Map.Entry<String, JsonElement>> entrySet = obj.entrySet();
		final Data.Builder builder = new Data.Builder();
		for (final Map.Entry<String, JsonElement> entry : entrySet) {
			final List<BrowserPattern> browserPatterns = deserializeType(context, entry, SerializableDataField.BROWSERPATTERNS,
					BrowserPattern.class);
			for (final BrowserPattern browserPattern : browserPatterns) {
				builder.appendBrowserPattern(browserPattern);
			}
			final List<BrowserType> browserTypes = deserializeType(context, entry, SerializableDataField.BROWSERTYPES, BrowserType.class);
			for (final BrowserType browserType : browserTypes) {
				builder.appendBrowserType(browserType);
			}
			final List<Browser> browsers = deserializeType(context, entry, SerializableDataField.BROWSERS, Browser.class);
			for (final Browser browser : browsers) {
				builder.appendBrowser(browser);
			}
			final List<OperatingSystemPattern> operatingSystemPatterns = deserializeType(context, entry,
					SerializableDataField.OPERATINGSYSTEMPATTERNS, OperatingSystemPattern.class);
			for (final OperatingSystemPattern operatingSystemPattern : operatingSystemPatterns) {
				builder.appendOperatingSystemPattern(operatingSystemPattern);
			}
			final List<OperatingSystem> operatingSystems = deserializeType(context, entry, SerializableDataField.OPERATINGSYSTEMS,
					OperatingSystem.class);
			for (final OperatingSystem operatingSystem : operatingSystems) {
				builder.appendOperatingSystem(operatingSystem);
			}
			final List<Robot> robots = deserializeType(context, entry, SerializableDataField.ROBOTS, Robot.class);
			for (final Robot robot : robots) {
				builder.appendRobot(robot);
			}
			if (SerializableDataField.VERSION.getName().equals(entry.getKey())) {
				builder.setVersion(entry.getValue().getAsString());
			}
		}

		// create data
		Data data = Data.EMPTY;
		try {
			data = builder.build();
		} catch (final Exception e) {
			addWarning(e.getLocalizedMessage());
		}

		return data;
	}

}
