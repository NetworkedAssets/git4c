var Git4COverlay = {
    getComponent: function (Events) {
        return {
            template: `
                <div style="position: relative; flex: 1; align-content: stretch;">
                    <div v-if="loading" class="git4c-component-overlay">
                        <div style="transform:translate(-50%,-50%);position: absolute; top:50%; left: 50%; width: 100%;">
                            <div id="spinner1" class="spinner6" style="width:60px; margin: 0 auto;">
                                <div class="rect1" style="background-color: rgb(204, 214, 7);"></div>
                                <div class="rect2" style="background-color: rgb(204, 214, 7);"></div>
                                <div class="rect3" style="background-color: rgb(204, 214, 7);"></div>
                                <div class="rect4" style="background-color: rgb(204, 214, 7);"></div>
                                <div class="rect5" style="background-color: rgb(204, 214, 7);"></div>
                            </div>
                            <div>
                                <p style="font-family: Arial; color: rgb(204, 214, 7); font-weight: bold; font-size: 20pt; text-align: center;">
                                    Loading content from git</p>
                            </div>
                            <div>
                                <p style="font-family: Arial; color: rgb(112, 112, 112); font-weight: normal; font-size: 14pt; text-align: center;">
                                    It shouldn't take more than one minute</p>
                            </div>
                        </div>
                    </div>
                    <div>
                        <slot></slot>
                    </div>
                </div>
            `,
            data: function () {
                return {
                    //It's intentional - first event isn't fast enough
                    loading: true
                };
            },

            mounted() {
                Events.$on("OverlayChange", (loading) => {
                    this.loading = loading
                })
            }
        }
    },

    getLoaderAlone: function (Events) {
        return {
            template: `
                <div style="position: relative; flex: 1; align-content: stretch;">
                    <div v-if="loading" class="git4c-component-overlay">
                        <div style="transform:translate(-50%,-50%);position: absolute; top:50%; left: 50%; width: 100%;">
                            <div id="spinner1" class="spinner6" style="width:60px; margin: 0 auto;">
                                <div class="rect1" style="background-color: rgb(204, 214, 7);"></div>
                                <div class="rect2" style="background-color: rgb(204, 214, 7);"></div>
                                <div class="rect3" style="background-color: rgb(204, 214, 7);"></div>
                                <div class="rect4" style="background-color: rgb(204, 214, 7);"></div>
                                <div class="rect5" style="background-color: rgb(204, 214, 7);"></div>
                            </div>
                        </div>
                    </div>
                    <div>
                        <slot></slot>
                    </div>
                </div>
            `,
            data: function () {
                return {
                    //It's intentional - first event isn't fast enough
                    loading: true
                };
            },

            mounted() {
                Events.$on("OverlayChange", (loading) => {
                    this.loading = loading
                })
            }
        }
    }

};
