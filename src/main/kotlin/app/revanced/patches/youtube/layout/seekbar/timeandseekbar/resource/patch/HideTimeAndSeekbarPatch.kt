package app.revanced.patches.youtube.layout.seekbar.timeandseekbar.resource.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.shared.annotation.YouTubeCompatibility
import app.revanced.patches.youtube.layout.seekbar.timeandseekbar.bytecode.patch.HideTimeAndSeekbarBytecodePatch
import app.revanced.patches.youtube.misc.settings.resource.patch.SettingsPatch
import app.revanced.util.resources.ResourceHelper

@Patch
@Name("hide-time-and-seekbar")
@Description("Hides progress bar and time counter on videos.")
@DependsOn(
    [
        HideTimeAndSeekbarBytecodePatch::class,
        SettingsPatch::class
    ]
)
@YouTubeCompatibility
@Version("0.0.1")
class HideTimeAndSeekbarPatch : ResourcePatch {
    override fun execute(context: ResourceContext): PatchResult {

        /*
         add settings
         */
        ResourceHelper.addSettings2(
            context,
            "PREFERENCE_CATEGORY: REVANCED_SETTINGS",
            "PREFERENCE: LAYOUT_SETTINGS",
            "PREFERENCE_HEADER: SEEKBAR",
            "SETTINGS: HIDE_TIME_AND_SEEKBAR"
        )

        ResourceHelper.patchSuccess(
            context,
            "hide-time-and-seekbar"
        )

        return PatchResultSuccess()
    }
}