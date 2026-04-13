document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll("[data-agent-picker]").forEach((widget) => {
        const search = widget.querySelector(".agent-search");
        const select = widget.querySelector(".agent-select");
        const chips = Array.from(widget.querySelectorAll(".agent-chip"));
        const options = Array.from(select.options).slice(1);

        const emptyNote = document.createElement("small");
        emptyNote.className = "agent-picker-note agent-picker-empty";
        emptyNote.textContent = "No agents match your search.";
        emptyNote.hidden = true;
        widget.appendChild(emptyNote);

        const normalize = (value) => (value || "").toLowerCase().trim();

        const syncActiveChip = () => {
            chips.forEach((chip) => {
                chip.classList.toggle("active", chip.dataset.agentValue === select.value);
            });
        };

        const applyFilter = () => {
            const term = normalize(search.value);
            let visibleCount = 0;

            options.forEach((option) => {
                const haystack = normalize(`${option.dataset.agentName || ""} ${option.dataset.agentCategory || ""} ${option.textContent || ""}`);
                const match = !term || haystack.includes(term);
                option.hidden = !match;
                if (match) {
                    visibleCount += 1;
                }
            });

            chips.forEach((chip) => {
                const haystack = normalize(`${chip.dataset.agentName || ""} ${chip.dataset.agentCategory || ""}`);
                const match = !term || haystack.includes(term);
                chip.classList.toggle("hidden", !match);
            });

            emptyNote.hidden = visibleCount !== 0;
        };

        search.addEventListener("input", applyFilter);
        select.addEventListener("change", syncActiveChip);

        chips.forEach((chip) => {
            chip.addEventListener("click", () => {
                select.value = chip.dataset.agentValue;
                select.dispatchEvent(new Event("change", { bubbles: true }));
            });
        });

        applyFilter();
        syncActiveChip();
    });
});
