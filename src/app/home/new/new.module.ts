import { NgModule, NO_ERRORS_SCHEMA } from '@angular/core'
import { NativeScriptCommonModule, NativeScriptRouterModule } from '@nativescript/angular'

import {NewComponent} from "./new.component";


@NgModule({
  imports: [
    NativeScriptCommonModule,
    NativeScriptRouterModule.forChild([{
      path:'',
      component:NewComponent,
    }]),
    NativeScriptRouterModule
  ],
  declarations: [
    NewComponent,
  ],
  schemas: [NO_ERRORS_SCHEMA],
})
export class NewModule {}
