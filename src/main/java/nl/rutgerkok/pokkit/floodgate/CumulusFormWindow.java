package nl.rutgerkok.pokkit.floodgate;

import cn.nukkit.GameVersion;
import cn.nukkit.form.response.FormResponse;
import cn.nukkit.form.window.FormWindow;

import java.util.function.Consumer;

/**
 * Adapter that wraps a Cumulus form as a Nukkit FormWindow.
 * Uses pre-computed JSON from Cumulus and routes responses back to Cumulus handlers.
 */
final class CumulusFormWindow extends FormWindow {

	private final String jsonData;
	private final Consumer<String> responseConsumer;

	CumulusFormWindow(String jsonData, Consumer<String> responseConsumer) {
		this.jsonData = jsonData;
		this.responseConsumer = responseConsumer;
	}

	@Override
	public String getJSONData(GameVersion gameVersion) {
		return jsonData;
	}

	@Override
	public void setResponse(int protocol, String data) {
		if ("null".equals(data)) {
			this.closed = true;
		}
		responseConsumer.accept(data);
	}

	@Override
	public FormResponse getResponse() {
		return null;
	}
}
