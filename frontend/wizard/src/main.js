import { createApp } from 'vue'
import App from './App.vue'
import PrimeVue from 'primevue/config'
import Aura from '@primevue/themes/aura'
import noDoubleSelect from './directives/noDoubleSelect'
import blobSpot from './directives/blobSpot'

import './components/styles/themes/colors.css'

const app = createApp(App)
app.directive('no-double-select', noDoubleSelect)
app.directive('blob-spot', blobSpot)
app.use(PrimeVue, { theme: { preset: Aura } })
app.mount('#app')
 