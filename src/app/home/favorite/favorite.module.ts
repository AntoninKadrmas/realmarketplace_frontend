import { NgModule, NO_ERRORS_SCHEMA } from '@angular/core'
import { NativeScriptCommonModule, NativeScriptRouterModule } from '@nativescript/angular'

import {FavoriteComponent} from "./favorite.component";

@NgModule({
  imports: [
    NativeScriptCommonModule,
    NativeScriptRouterModule.forChild([{
      path:'',
      component:FavoriteComponent,
    }]),
    NativeScriptRouterModule
  ],
  declarations: [
    FavoriteComponent,
  ],
  schemas: [NO_ERRORS_SCHEMA],
})
export class FavoriteModule {}
