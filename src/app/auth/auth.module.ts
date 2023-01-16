import { NgModule, NO_ERRORS_SCHEMA } from '@angular/core'
import { NativeScriptCommonModule } from '@nativescript/angular'

import {AuthComponent} from "./auth.component";
import {AuthRoutingModule} from "./auth-routing.module";

@NgModule({
  imports: [NativeScriptCommonModule, AuthRoutingModule],
  declarations: [AuthComponent],
  schemas: [NO_ERRORS_SCHEMA],
})
export class AuthModule {}
